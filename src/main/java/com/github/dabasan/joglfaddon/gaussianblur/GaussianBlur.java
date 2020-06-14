package com.github.dabasan.joglfaddon.gaussianblur;

import static com.github.dabasan.joglf.gl.wrapper.GLWrapper.*;
import static com.jogamp.opengl.GL.*;

import com.github.dabasan.joglf.gl.shader.ShaderProgram;
import com.github.dabasan.joglf.gl.transferrer.FullscreenQuadTransferrerWithUV;
import com.github.dabasan.joglf.gl.util.screen.Screen;
import com.github.dabasan.joglf.gl.util.screen.ScreenBase;

/**
 * Applies Gaussian blur to screens.<br>
 * <br>
 * Gaussian blur is applied first horizontally, then vertically.<br>
 * Horizontally blurred data is stored in a screen instance, and the size of the
 * screen instance is determined in the constructor according to its arguments.
 * 
 * @author Daba
 *
 */
public class GaussianBlur {
	private ShaderProgram program;
	private FullscreenQuadTransferrerWithUV transferrer;
	private Screen screen_intermediate;

	private float[] weights;

	/**
	 * 
	 * @param width
	 *            Width of the buffer screen
	 * @param height
	 *            Height of the buffer screen
	 */
	public GaussianBlur(int width, int height) {
		program = new ShaderProgram("dabasan/gaussian_blur",
				"./Data/Shader/330/addon/dabasan/gaussian_blur/vshader.glsl",
				"./Data/Shader/330/addon/dabasan/gaussian_blur/fshader.glsl");
		transferrer = new FullscreenQuadTransferrerWithUV(false);
		screen_intermediate = new Screen(width, height);

		weights = new float[10];
		this.GenerateWeights(4.0f);
	}

	/**
	 * Generate weights for blur processing.
	 * 
	 * @param c
	 *            Standard deviation
	 */
	public void GenerateWeights(float c) {
		float c2 = c * c;
		float sum = 0.0f;
		for (int i = 0; i < weights.length; i++) {
			float x2 = (float) (i * i);
			float w = (float) Math.exp(-0.5f * x2 / c2);
			weights[i] = w;
			if (i > 0) {
				w *= 2.0f;
			}
			sum += w;
		}

		for (int i = 0; i < weights.length; i++) {
			weights[i] /= sum;
		}

		program.Enable();
		for (int i = 0; i < weights.length; i++) {
			String uname = "weights" + "[" + i + "]";
			program.SetUniform(uname, weights[i]);
		}
		program.Disable();
	}

	/**
	 * Applies blur to a screen.
	 * 
	 * @param src
	 *            Source screen
	 * @param dst
	 *            Destination screen
	 */
	public void ApplyBlur(ScreenBase src, ScreenBase dst) {
		program.Enable();
		// Horizontal blur
		program.SetUniform("direction", BlurDirection.HORIZONTAL.ordinal());
		glActiveTexture(GL_TEXTURE0);
		src.BindScreenTexture();
		program.SetUniform("texture_sampler", 0);
		program.SetUniform("texture_size", src.GetScreenWidth(), src.GetScreenHeight());
		screen_intermediate.Enable();
		screen_intermediate.Clear();
		transferrer.Transfer();
		screen_intermediate.Disable();
		// Vertical blur
		program.SetUniform("direction", BlurDirection.VERTICAL.ordinal());
		glActiveTexture(GL_TEXTURE0);
		screen_intermediate.BindScreenTexture();
		program.SetUniform("texture_sampler", 0);
		program.SetUniform("texture_size", screen_intermediate.GetScreenWidth(),
				screen_intermediate.GetScreenHeight());
		dst.Enable();
		dst.Clear();
		transferrer.Transfer();
		dst.Disable();
		program.Disable();
	}
}
