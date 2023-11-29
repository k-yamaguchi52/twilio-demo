package com.goalist.twiliodemo.controller;

import com.goalist.twiliodemo.repository.CompanyTelRepository;
import com.twilio.Twilio;
import com.twilio.http.HttpMethod;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import com.twilio.rest.proxy.v1.Service;
import com.twilio.rest.proxy.v1.service.PhoneNumber;
import com.twilio.rest.proxy.v1.service.Session;
import com.twilio.rest.proxy.v1.service.session.Participant;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwilioController {

    @Autowired
    private CompanyTelRepository companyTelRepository;

    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";
    public static final String PHONE_NUMBER_SID = "";

//    求人をクリックした時に呼ばれる
//    電話番号を用意して、その電話番号に求職者は電話をかける
    @GetMapping("/getTempNumber")
    public String getTempNumber(@RequestParam("locationId") Integer locationId) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // すでにある電話番号のwebhookを変更しているが、ここで新しい電話番号を購入しても良い
        IncomingPhoneNumber incomingPhoneNumber = IncomingPhoneNumber.updater(PHONE_NUMBER_SID)
//                求職者が電話をかけた場合に呼び出すエンドポイントをwebhookに設定
                .setVoiceUrl("https://1e86-157-14-97-20.ngrok-free.app/voiceWebHook?companyId=" + locationId)
                .setVoiceMethod(HttpMethod.GET)
                .update();
        return incomingPhoneNumber.getPhoneNumber().toString();
    }

    @GetMapping("voiceWebHook")
    public String voiceWebHook(@RequestParam("locationId") Integer locationId) {
        String phoneNumber = companyTelRepository.retrieveCompanyTel(locationId);
        Dial dial = new Dial.Builder().number(phoneNumber).build();
        VoiceResponse response = new VoiceResponse.Builder().dial(dial).build();
        return response.toXml();
    }



    @GetMapping("/getProxyNumber")
    public String getProxyNumber() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Service proxyService = Service
                .creator("My First Proxy Service")
                .create();
        PhoneNumber phoneNumber =
                PhoneNumber.creator(proxyService.getSid())
                        .setSid(PHONE_NUMBER_SID)
                        .create();
        Session session = Session.creator(proxyService.getSid())
                .setUniqueName("MyFirstSession")
                .create();
        Participant participant = Participant
                .creator(proxyService.getSid(), session.getSid(), "+15005550006")
                .setFriendlyName("Company")
                .create();
        return participant.getProxyIdentifier();
    }
}
