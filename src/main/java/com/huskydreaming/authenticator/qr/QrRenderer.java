package com.huskydreaming.authenticator.qr;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class QrRenderer extends MapRenderer {

    private boolean rendered;
    private final byte[] data;

    public QrRenderer(byte[] data) {
        this.data = data;
    }

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        if (rendered) return;

        BufferedImage bufferedImage = createImage(data);
        if (bufferedImage != null) {
            mapCanvas.drawImage(0, 0, bufferedImage);
            rendered = true;
        }
    }

    private BufferedImage setupImage(byte[] data) throws IOException {
        int size = 128;

        final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
        final BufferedImage finalImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = finalImage.createGraphics();

        graphics.drawImage(bufferedImage, 0, 0, size, size, null);
        graphics.dispose();

        return finalImage;
    }

    private BufferedImage createImage(byte[] data) {
        boolean useCache = ImageIO.getUseCache();

        BufferedImage bufferedImage = null;
        try {
            ImageIO.setUseCache(false);
            bufferedImage = setupImage(data);
            ImageIO.setUseCache(useCache);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bufferedImage;
    }
}
