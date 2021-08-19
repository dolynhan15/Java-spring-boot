package com.example.hellospringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class GirlFriend {
    @Autowired
    @Qualifier("bikini1")
    public Outfit outfit;
    public GirlFriend(){
    }
    public GirlFriend(Outfit outfit){
        this.outfit = outfit;
    }
    public void setOutfit(Outfit outfit) {
        this.outfit = outfit;
    }
    public Outfit getOutfit(){
        return outfit;
    }
}
