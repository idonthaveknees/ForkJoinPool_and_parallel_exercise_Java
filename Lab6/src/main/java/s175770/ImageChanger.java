package s175770;

import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.function.Function;

public class ImageChanger implements Function<Pair<String, BufferedImage>, Pair<String, BufferedImage>> {
    Pair<String, BufferedImage> processImage(Pair<String, BufferedImage> original) {
        BufferedImage image = new BufferedImage(original.getRight().getWidth(),
                original.getRight().getHeight(),
                original.getRight().getType());
        for(int i = 0; i < original.getRight().getWidth(); i++) {
            for (int j = 0; j < original.getRight().getHeight(); j++) {
                int rgb = original.getRight().getRGB(i, j);
                Color color = new Color(rgb);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                Color outColor = new Color(red, blue, green);
                int outRgb = outColor.getRGB();
                image.setRGB(i, j, outRgb);
            }
        }
        return Pair.of(original.getLeft(),image);
    }

    @Override
    public Pair<String, BufferedImage> apply(Pair<String, BufferedImage> stringBufferedImagePair) {
        return processImage(stringBufferedImagePair);
    }
}
