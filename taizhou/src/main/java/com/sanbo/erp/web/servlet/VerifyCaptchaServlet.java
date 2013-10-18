package com.sanbo.erp.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.infoware.captacha.ImageCaptcha;
import com.infoware.captacha.encode.JPEGOutputEncoder;
import com.infoware.captacha.encode.OutputEncoder;
import com.infoware.captacha.generator.image.DefaultImageCaptachaGenerator;
import com.infoware.captacha.generator.image.ImageCaptchaGenerator;
import com.infoware.captacha.generator.image.SimpleTextToImage;
import com.infoware.captacha.generator.image.TextToImage;
import com.infoware.captacha.generator.word.RandomWordGenerator;
import com.infoware.captacha.generator.word.WordGenerator;
import com.infoware.captacha.web.servlet.SimpleCaptchaServlet;

@SuppressWarnings("serial")
public class VerifyCaptchaServlet extends SimpleCaptchaServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		ServletOutputStream out = response.getOutputStream();

		WordGenerator wordGenerator = new RandomWordGenerator("0123456789", 4);
		TextToImage textToImage = new SimpleTextToImage(70, 20);
		ImageCaptchaGenerator captchaGenerator = new DefaultImageCaptachaGenerator(70, 20, textToImage, wordGenerator);

		ImageCaptcha imageCaptcha = captchaGenerator.getWordImage();
		session.setAttribute(SESSION_VALIDATE_CODE, imageCaptcha.getWord());
		OutputEncoder outputEncoder = new JPEGOutputEncoder();
		outputEncoder.encode(imageCaptcha.getImage(), out);
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}
}
