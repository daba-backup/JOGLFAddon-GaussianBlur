package com.github.dabasan.joglfaddon.gaussianblur;

import static com.github.dabasan.basis.vector.VectorFunctions.*;
import static com.github.dabasan.joglf.gl.wrapper.GLWrapper.*;
import static com.jogamp.opengl.GL.*;

import com.github.dabasan.joglf.gl.front.CameraFront;
import com.github.dabasan.joglf.gl.input.keyboard.KeyboardEnum;
import com.github.dabasan.joglf.gl.model.Model3DFunctions;
import com.github.dabasan.joglf.gl.util.screen.Screen;
import com.github.dabasan.joglf.gl.window.JOGLFWindow;

class TestWindow extends JOGLFWindow {
	private int[] model_handles;
	private Screen screen_src;
	private Screen screen_dst;
	private GaussianBlur blur;

	public TestWindow() {
		super(1280, 720, "TestWindow", true);
	}

	@Override
	public void Init() {
		model_handles = new int[2];
		model_handles[0] = Model3DFunctions.LoadModel("./Data/Model/OBJ/Teapot/teapot.obj");
		model_handles[1] = Model3DFunctions.LoadModel("./Data/Model/OBJ/Plane/plane.obj");

		screen_src = new Screen(this.GetWidth(), this.GetHeight());
		screen_dst = new Screen(this.GetWidth(), this.GetHeight());
		blur = new GaussianBlur(this.GetWidth(), this.GetHeight());
		blur.GenerateWeights(4.0f);

		glDisable(GL_CULL_FACE);
	}

	@Override
	public void Update() {
		CameraFront.SetCameraPositionAndTarget_UpVecY(VGet(35.0f, 50.0f, 35.0f),
				VGet(0.0f, 10.0f, 0.0f));
	}

	@Override
	public void Draw() {
		screen_src.Enable();
		screen_src.Clear();
		Model3DFunctions.DrawModel(model_handles[0]);
		Model3DFunctions.DrawModel(model_handles[1]);
		screen_src.Disable();

		blur.ApplyBlur(screen_src, screen_dst);

		screen_dst.Draw(0, 0, this.GetWidth(), this.GetHeight());

		if (this.GetKeyboardPressingCount(KeyboardEnum.KEY_ENTER) == 1) {
			screen_src.TakeScreenshot("src.png");
			screen_dst.TakeScreenshot("dst.png");
		}
	}
}
