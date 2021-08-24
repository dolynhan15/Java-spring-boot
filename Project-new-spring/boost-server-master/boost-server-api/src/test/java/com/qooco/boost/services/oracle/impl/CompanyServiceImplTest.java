package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.reposistories.CompanyRepository;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.data.oracle.services.impl.CompanyServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CompanyServiceImplTest {

    @InjectMocks
    private CompanyService companyService = new CompanyServiceImpl();

    @Mock
    private CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);

//    @Test
//    public void getCompanies_whenUserProfileIdIsNotExistedInSystem_thenReturnCompanyRespIsNull() {
//        Company company = companyService.getCompanyByUserProfileId(1000000L);
//        Assert.assertNull(company);
//    }
//
//    @Test
//    public void getCompanies_whenUserProfileIdIsExistedInSystem_thenReturnCompanyRespSuccess() {
//        Company expectedCompany = new Company(12345L);
//        Mockito.when(companyRepository.findCompanyByUserProfileId(12345L)).thenReturn(expectedCompany);
//        Company actualCompany = companyService.getCompanyByUserProfileId(12345L);
//        Assert.assertEquals(expectedCompany, actualCompany);
//    }

    @Test
    public void getCompanyById_whenHaveValidCompanyId_thenReturnCompany() {
        Company expectedCompany = new Company(1L);

        Mockito.when(companyRepository.findValidById(1L))
                .thenReturn(expectedCompany);

        Company actualCompany = companyService.findById(1L);
        Assert.assertEquals(expectedCompany, actualCompany);
    }

    @Test
    public void getCompanyById_whenHaveInvalidCompanyId_thenReturnCompany() {
        Mockito.when(companyRepository.findValidById(-1L))
                .thenReturn(null);

        Company actualCompany = companyService.findById(1L);
        Assert.assertNull(actualCompany);
    }

    @Test
    public void updateCompanyStatus_whenCompanyIdIsNotExistedInSystem_thenReturnNotFoundError() {
        Mockito.when(companyRepository.updateCompanyStatusByCompanyId(1L)).thenReturn(0);
        Assert.assertEquals(0, companyService.updateCompanyStatus(1L).intValue());

    }

    @Test
    public void updateCompanyStatus_whenCompanyIdIsExistedInSystem_thenReturnSuccess() {
        Mockito.when(companyRepository.updateCompanyStatusByCompanyId(1L)).thenReturn(1);
        Assert.assertEquals(1, companyService.updateCompanyStatus(1L).intValue());
    }
}