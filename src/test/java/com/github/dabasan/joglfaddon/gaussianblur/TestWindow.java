package com.github.dabasan.joglfaddon.gaussianblur;

import static com.github.dabasan.basis.vector.VectorFunctions.*;

import com.github.dabasan.joglf.gl.front.CameraFront;
import com.github.dabasan.joglf.gl.model.Model3DFunctions;
import com.github.dabasan.joglf.gl.util.screen.Screen;
import com.github.dabasan.joglf.gl.window.JOGLFWindow;

class TestWindow extends JOGLFWindow {
	private int model_handle;
	private Screen screen_src;
	private Screen screen_dst;
	private GaussianBlur blur;

	public TestWindow(int width, int height) {
		super(width, height, "TestWindow", true);
	}

	@Override
	public void Init() {
		final float SCALE = 1.7f / 20.0f;
		model_handle = Model3DFunctions.LoadModel("./Data/Model/BD1/LeisureHouse/map.bd1");
		Model3DFunctions.RescaleModel(model_handle, VGet(SCALE, SCALE, SCALE));

		screen_src = new Screen(this.GetWidth(), this.GetHeight());
		screen_dst = new Screen(this.GetWidth(), this.GetHeight());
		blur = new GaussianBlur(this.GetWidth(), this.GetHeight());
	}

	@Override
	public void Update() {
		CameraFront.SetCameraPositionAndTarget_UpVecY(VGet(35.0f, 35.0f, 35.0f),
				VGet(0.0f, 0.0f, 0.0f));
	}

	@Override
	public void Draw() {
		screen_src.Enable();
		screen_src.Clear();
		Model3DFunctions.DrawModel(model_handle);
		screen_src.Disable();

		blur.ApplyBlur(screen_src, screen_dst);

		screen_dst.Draw(0, 0, this.GetWidth(), this.GetHeight());
	}
}
