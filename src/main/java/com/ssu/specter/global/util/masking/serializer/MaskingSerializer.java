package com.ssu.specter.global.util.masking.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ssu.specter.global.config.security.jwt.JwtClaimsPayload;
import com.ssu.specter.global.util.StringUtil;
import com.ssu.specter.global.util.masking.EmailMasking;
import com.ssu.specter.global.util.masking.PersonNameMasking;
import com.ssu.specter.global.util.masking.TelephoneNumberMasking;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MaskingSerializer {
    /**
     * 이메일 마스킹
     */
    @NoArgsConstructor
    public static class Email extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (StringUtil.isEmpty(value)) {
                gen.writeNull();
            } else {
                gen.writeString(maskingEmail(value));
            }
        }

        private String maskingEmail(String value) {
            return JwtClaimsPayload.isPrivateInfoUser() ? value : EmailMasking.getInstance().apply(value);
        }
    }

    /**
     * 전화번호 마스킹
     */
    @NoArgsConstructor
    public static class Telephone extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (StringUtil.isEmpty(value)) {
                gen.writeNull();
            } else {
                gen.writeString(maskingTelephone(value));
            }
        }

        private String maskingTelephone(String value) {
            return JwtClaimsPayload.isPrivateInfoUser() ? value : TelephoneNumberMasking.getInstance().apply(value);
        }
    }

    /**
     * 이름 마스킹
     */
    @NoArgsConstructor
    public static class Name extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (StringUtil.isEmpty(value)) {
                gen.writeNull();
            } else {
                gen.writeString(maskingName(value));
            }
        }

        private String maskingName(String value) {
            return JwtClaimsPayload.isPrivateInfoUser() ? value : PersonNameMasking.getInstance().apply(value);
        }
    }
}
