package com.qooco.boost.business.impl;

import com.itextpdf.text.DocumentException;
import com.qooco.boost.business.FileStorageService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.data.oracle.services.PataFileService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.enumeration.StoreFilePurpose;
import com.qooco.boost.exception.FileNotFoundException;
import com.qooco.boost.models.request.message.MediaMessageReq;
import com.qooco.boost.models.user.UserCvPrint;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.FileUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.itextpdf.text.pdf.BaseFont.EMBEDDED;
import static com.itextpdf.text.pdf.BaseFont.IDENTITY_H;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    protected Logger logger = LogManager.getLogger(FileStorageServiceImpl.class);
    private static final String THUMBNAIL = "thumbnail";
    private static final String UTF_8 = "UTF-8";

    @Value(ApplicationConstant.BOOST_FILE_UPLOAD_DIR)
    private String rootPath = "";

    @Autowired
    private PataFileService pataFileService;

    @Autowired
    private TemplateEngine templateEngine;

    @PostConstruct
    private void init() {
        if (!FileUtils.checkExistedFolder(rootPath)) {
            rootPath = FileUtils.createRootFolder(rootPath);
        }
    }

    @Override
    public List<PataFile> storeFile(Long userProfileId, String subPath, List<MultipartFile> files) {

        FileUtils.createFolder(rootPath, subPath);

        //Do save file to disk store. Need to check disk and break
        List<PataFile> results = new ArrayList<>();
        for (MultipartFile file : files) {
            String _path = saveFile(rootPath, subPath, file);
            if (StringUtils.isNotBlank(_path)) {
                results.add(createPataFile(userProfileId, _path, file));
            }
        }
        //Do save to DB
        if (CollectionUtils.isNotEmpty(results)) {
            pataFileService.save(results);
        }

        return results;
    }

    @Override
    public PataFile storeFile(Long userProfileId, String subPath, MediaMessageReq mediaMessageReq) {
        FileUtils.createFolder(rootPath, subPath);

        //Do save file to disk store. Need to check disk and break
        String filePath = saveFile(rootPath, subPath, mediaMessageReq.getFile());
        if (StringUtils.isNotBlank(filePath)) {
            PataFile pataFile = createPataFile(userProfileId, filePath, mediaMessageReq.getFile());
            String thumbnailPath = saveThumbnail(subPath, mediaMessageReq.getThumbnail(), FilenameUtils.getBaseName(filePath));

            pataFile.setThumbnailUrl(thumbnailPath);
            pataFile.setPurpose(StoreFilePurpose.FOR_MESSAGE.getCode());
            pataFile.setDuration(mediaMessageReq.getDuration());
            return pataFileService.save(pataFile);
        }
        return null;
    }

    private String saveThumbnail(String subPath, MultipartFile file, String originalBaseName) {
        if (Objects.nonNull(file)) {
            String thumbnailName = FileUtils.createFileName(originalBaseName, THUMBNAIL, FilenameUtils.getExtension(file.getOriginalFilename()));
            String filePath = saveFile(rootPath, subPath, thumbnailName, file);
            if (StringUtils.isNotBlank(filePath)) {
                return filePath;
            }
        }
        return null;
    }

    @Override
    public Resource loadResource(String filePath) {

        try {
            Path _filePath = Paths.get(StringUtil.append(rootPath, filePath));
            Resource resource = new UrlResource(_filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
        } catch (MalformedURLException ex) {
            logger.warn(ex);
            throw new FileNotFoundException(ResponseStatus.NOT_FOUND, ex);
        }
        return null;
    }

    @Override
    public void deleteExpiredFile(Date expiredDate) {
        if (Objects.nonNull(expiredDate)) {
            emptyIfNull(pataFileService.findByPurposeAndCreatedDate(StoreFilePurpose.FOR_MESSAGE.getCode(), DateUtils.toUtcForOracle(expiredDate)))
                    .stream()
                    .peek(it -> deleteFileByRelativePath(it.getUrl()))
                    .map(PataFile::getThumbnailUrl)
                    .filter(Objects::nonNull)
                    .forEach(this::deleteFileByRelativePath);
        }
    }

    @Override
    public void deleteFileById(Long id) {
        PataFile pataFile = pataFileService.findById(id);
        Optional.ofNullable(pataFile).orElseThrow(() -> new FileNotFoundException(ResponseStatus.NOT_FOUND));
        String path = StringUtil.append(rootPath, pataFile.getUrl());
        FileUtils.deleteFile(path);
        pataFileService.delete(id);
    }

    @Override
    public void deleteFileByAbsolutePath(String absolutePath) {
        String relativePath = ServletUriUtils.getRelativePath(absolutePath);
        deleteFileByRelativePath(relativePath);
    }

    @Override
    public void deleteFileByRelativePath(String relativePath) {
        pataFileService.delete(relativePath);
        FileUtils.deleteFile(StringUtil.append(rootPath, relativePath));
    }

    @Override
    public String createPdfFile(UserCvPrint userCvPrint, Long userProfileId, String subPath) {
        FileUtils.createFolder(rootPath, subPath);
        String fileName = String.format("User-CV-PDF-%s.pdf",RandomStringUtils.randomAlphanumeric(8));
        String relativePath = StringUtil.append(FileUtils.FILE_SEPARATOR, subPath, FileUtils.FILE_SEPARATOR, fileName);

//        templateEngine.setTemplateResolver(templateResolver);

        // The data in our Thymeleaf templates is not hard-coded. Instead,
        // we use placeholders in our templates. We fill these placeholders
        // with actual data by passing in an object. In this example, we will
        // write a letter to "John Doe".
        //
        // Note that we could also read this data from a JSON file, a database
        // a web service or whatever.

        Context context = new Context();
        context.setVariable("data", userCvPrint);
        String baseUrl = getCurrentBaseUrl();
        userCvPrint.setBaseUrl(baseUrl);
        context.setVariable("baseUrl", baseUrl);
        logger.debug("baseUrl = "+baseUrl);

        // Flying Saucer needs XHTML - not just normal HTML. To make our life
        // easy, we use JTidy to convert the rendered Thymeleaf templates to
        // XHTML. Note that this might not work for very complicated HTML. But
        // it's good enough for a simple letter.
        String renderedHtmlContent = templateEngine.process("user-cv-test", context);
        String xHtml;
        try {
            xHtml = convertToXhtml(renderedHtmlContent);
            ITextRenderer renderer = new ITextRenderer(20, 20);
            renderer.getFontResolver().addFont(baseUrl+"/fonts/arial-unicode-ms.ttf", IDENTITY_H, EMBEDDED);
            // FlyingSaucer has a working directory. If you run this test, the working directory
            // will be the root folder of your project. However, all files (HTML, CSS, etc.) are
            // located under "/src/test/resources". So we want to use this folder as the working
            // directory.
//            String baseUrl = FileSystems
//                    .getDefault()
//                    .getPath("src", "main", "resources/templates")
//                    .toUri()
//                    .toURL()
//                    .toString();

            renderer.setDocumentFromString(xHtml);


            renderer.layout();


            // And finally, we create the PDF:
            String absoluteFile = new StringBuffer(rootPath).append(relativePath).toString();
            OutputStream outputStream = new FileOutputStream(absoluteFile);
            renderer.createPDF(outputStream);
            outputStream.close();
            return ServletUriUtils.getAbsolutePath(relativePath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return "";
    }
    private static String getCurrentBaseUrl() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
    }


    private PataFile createPataFile(Long userProfileId, String resultPath, MultipartFile file) {
        return PataFile.builder()
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .createdDate(new Date())
                .userProfileId(userProfileId)
                .url(resultPath).build();
    }

    private String saveFile(String root, String subPath, MultipartFile file) {
        String fileName = FileUtils.removeInvalidCharacter(file.getOriginalFilename());
        fileName = FileUtils.createFileName(fileName);
        return saveFile(root, subPath, fileName, file);
    }

    private String saveFile(String root, String subPath, String fileName, MultipartFile file) {
        String relativePath = StringUtil.append(FileUtils.FILE_SEPARATOR, subPath, FileUtils.FILE_SEPARATOR, fileName);
        if (FileUtils.saveFile(root, relativePath, file)) {
            return relativePath;
        }
        return "";
    }

    private String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(UTF_8);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(UTF_8);
    }

}
