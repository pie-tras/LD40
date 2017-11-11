package audio;

import java.io.IOException;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		AL10.alDistanceModel(AL11.AL_EXPONENT_DISTANCE);
		
		int buffer = AudioMaster.loadSound("audio/PickUp.wav");
		Source source = new Source();
		source.setLooping(true);
		source.play(buffer);
		
		float xPos = 10;
		source.setPosition(xPos, 0, 2);
		
		char c = ' ';
		while(c != 'q') {
			
			xPos -= 0.02f;
			source.setPosition(xPos, 0, 2);
			System.out.println(xPos);
			Thread.sleep(10);
			
		}
		
		source.delete();
		AudioMaster.cleanUp();
		
	}
	
}
