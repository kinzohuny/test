package com.btw.server.servlet.pub;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.btw.server.constant.Constants;

/**
 * 随机码生成
 */
public class RandomCodeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 90;
	private static final int HEIGHT = 42;

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("image/jpeg");

		// 防止浏览器缓冲
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		HttpSession session = request.getSession();

		BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();

		char[] rands = getCode();

		drawBackground(g);
		drawRands(g, rands);

		g.dispose();

		// 使用ImageIO
		ServletOutputStream out = response.getOutputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "JPEG", bos);
		byte[] buf = bos.toByteArray();
		response.setContentLength(buf.length);
		out.write(buf); // bos.writeTo(sos);
		bos.close();
		out.close();

		session.setAttribute(Constants.SESSION_RANDOM, new String(rands));
	}

	/**
	 * 产生四位随机数
	 * 
	 * @return
	 */
	private char[] getCode() {
//		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ012356789";
		String chars = "abcdefghijkmnopqrstuvwxyz012356789";
		char[] rands = new char[4];
		for (int i = 0; i < rands.length; i++) {
			int rand = (int) (Math.random() * chars.length());
			rands[i] = chars.charAt(rand);
		}
		return rands;
	}

	/**
	 * 绘制背景
	 * 
	 * @param g
	 */
	private void drawBackground(Graphics g) {
		g.setColor(new Color(238, 238, 238));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		Random random = new Random();
		g.setColor(Color.BLACK);
        for (int i = 0; i < 10; i++) {        
            int x = random.nextInt(WIDTH);        
            int y = random.nextInt(HEIGHT);        
            int xl = random.nextInt(16);        
            int yl = random.nextInt(16);        
            g.drawLine(x, y, x + xl, y + yl);        
        }     
	}

	/**
	 * 绘制验证码
	 * 
	 * @param g
	 * @param rands
	 */
	private void drawRands(Graphics g, char[] rands) {
		int red = (int) (Math.random() * WIDTH);
		int green = (int) (Math.random() * WIDTH);
		int blue = (int) (Math.random() * WIDTH);

		g.setColor(new Color(red, green, blue));

		g.setFont(new Font("宋体", Font.ITALIC | Font.ITALIC, 25));
		g.drawString("" + rands[0], 10, 28);
		g.drawString("" + rands[1], 25, 29);
		g.drawString("" + rands[2], 43, 29);
		g.drawString("" + rands[3], 62, 29);
	}

}
