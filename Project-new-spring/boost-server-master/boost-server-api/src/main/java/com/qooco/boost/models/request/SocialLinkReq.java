package com.qooco.boost.models.request;

public class SocialLinkReq {
    private String socialLink;

    public SocialLinkReq() {}

    public SocialLinkReq(String socialLink) {
        this.socialLink = socialLink;
    }

    public String getSocialLink() {
        return socialLink;
    }

    public void setSocialLink(String socialLink) {
        this.socialLink = socialLink;
    }
}