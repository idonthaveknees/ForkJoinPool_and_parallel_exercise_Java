package s175770;

import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static List<Pair<String, BufferedImage>> changedImages;

    static List<Pair<String, BufferedImage>> loadImages() throws IOException {
        List<Path> files = new ArrayList<>();
        Path source = Path.of("src\\main\\resources\\original_images");

        try (Stream<Path> stream = Files.list(source)) {
            files = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Pair<String, BufferedImage>> originalImages = new ArrayList<>();
        for (Path file : files) {
            Pair<String, BufferedImage> fullPair = Pair.of(file.toString(), ImageIO.read(file.toFile()));
            originalImages.add(fullPair);
        }
        return originalImages;
    }

    static void saveImages() throws IOException {
        for( Pair<String, BufferedImage> pair : changedImages) {
            String name = pair.getLeft();
            BufferedImage image = pair.getRight();

            String format = name.replaceAll(".+\\.", "");
            String[] parts = name.split("\\\\");
            String nameWithoutPath = parts[4].trim();

            File changedImageFile = Path.of("src\\main\\resources\\changed_images\\"+nameWithoutPath).toFile();
            ImageIO.write(image, format, changedImageFile);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Pair<String, BufferedImage>> originalImages = loadImages();

        ForkJoinPool pool = new ForkJoinPool(5);
        long time = System.currentTimeMillis();
        ImageChanger imageChanger = new ImageChanger();
        try {
            pool.submit(() -> {
                changedImages = originalImages.stream().parallel().map(imageChanger).collect(Collectors.toList());
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - time);
        pool.shutdown();

        saveImages();
    }
}
