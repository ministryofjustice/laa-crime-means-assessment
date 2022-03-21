package uk.gov.justice.laa.crime.meansassessment.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ClientSecretGeneratorUtil {
	public static void main(String[] args) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
		String encodedPassword = passwordEncoder.encode("sample");
		System.out.println(encodedPassword);
	}
}
