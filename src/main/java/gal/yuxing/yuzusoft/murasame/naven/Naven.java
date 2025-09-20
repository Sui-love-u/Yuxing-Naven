package gal.yuxing.yuzusoft.murasame.naven;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.ViaMCP;
import dev.yalan.live.LiveClient;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventClientInit;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventShader;
import gal.yuxing.yuzusoft.murasame.naven.ui.AltManager.NetEaseAltManager.AltManager;
import gal.yuxing.yuzusoft.murasame.naven.utils.*;
import gal.yuxing.yuzusoft.murasame.naven.utils.Managers.BlinkComponent;
import lombok.Getter;
import gal.yuxing.yuzusoft.murasame.naven.commands.CommandManager;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventManager;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.files.FileManager;
import gal.yuxing.yuzusoft.murasame.naven.protocols.MythProtocol;
import gal.yuxing.yuzusoft.murasame.naven.protocols.germ.GermMod;
import gal.yuxing.yuzusoft.murasame.naven.protocols.world.Wrapper;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleManager;
import gal.yuxing.yuzusoft.murasame.naven.ui.cooldown.CooldownBarManager;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.NotificationManager;
import gal.yuxing.yuzusoft.murasame.naven.utils.font.FontManager;
import gal.yuxing.yuzusoft.murasame.naven.values.HasValueManager;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.Main;
import net.minecraft.client.shader.Framebuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.awt.AlphaComposite;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;

import static gal.yuxing.yuzusoft.murasame.naven.utils.Utils.mc;

@ControlFlowObfuscation
@NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
public class Naven {
    private static final int EXPIRY_MONTH = 9;
    private static final int EXPIRY_DAY = 1;

    private static LocalDate getJarBuildDate() {
        try {
            URL jarUrl = Naven.class.getProtectionDomain().getCodeSource().getLocation();
            Path jarPath = Paths.get(jarUrl.toURI());
            FileTime lastModifiedTime = Files.getLastModifiedTime(jarPath);
            Instant instant = lastModifiedTime.toInstant();
            return instant.atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
    private static final LocalDate EXPIRY_DATE = getJarBuildDate().plusDays(6);
    /*
    //    private static final int EXPIRY_MONTH = EXPIRY_DATE.getMonthValue();
    //    private static final int EXPIRY_DAY = EXPIRY_DATE.getDayOfMonth();
    //    public static void main(String[] args) {
    //        System.out.println("构建日期: " + getJarBuildDate());
    //        System.out.println("过期日期: " + EXPIRY_DATE);
    //        System.out.println("EXPIRY_MONTH = " + EXPIRY_MONTH);
    //        System.out.println("EXPIRY_DAY = " + EXPIRY_DAY);
    //    }
    */
    private static final String TIME_API_URL = "https://cn.apihz.cn/api/time/getapi.php?id=10007008&key=YUXINGMURASAME&type=1";
    private static final int MAX_TIME_DIFF = 50;

    public static String CLIENT_NAME = "Naven";
    public static String CLIENT_DISPLAY_NAME = "Naven";
    private static final Logger logger = LogManager.getLogger(Naven.class);

    private static final Color DIALOG_BACKGROUND = new Color(20, 24, 35);
    private static final Color PANEL_START_COLOR = new Color(34, 39, 55);
    private static final Color PANEL_END_COLOR = new Color(28, 33, 48);
    private static final Color TEXT_COLOR = new Color(240, 245, 255);
    private static final Color TITLE_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(60, 70, 90);
    private static final Color OPTION_PANEL_START = new Color(45, 51, 70);
    private static final Color OPTION_PANEL_END = new Color(40, 46, 64);
    @Getter
    private static Naven instance;
    private final TimeHelper blurTimer = new TimeHelper();
    private final TimeHelper shadowTimer = new TimeHelper();
    private Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);
    @Getter
    public static VideoPlayer videoPlayer;
    @Getter
    private ModuleManager moduleManager;
    @Getter
    private EventManager eventManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private FileManager fileManager;
    @Getter
    private ValueManager valueManager;
    @Getter
    private HasValueManager hasValueManager;
    @Getter
    private FontManager fontManager;
    @Getter
    private NotificationManager notificationManager;
    @Getter
    private CooldownBarManager cooldownBarManager;

    private BlinkComponent blinkComponent;

    public AltManager altManager;

    private static final int ANIMATION_DURATION = 300;
    private static final float SCALE_START = 0.9f;
    private static final float SCALE_END = 1.0f;

    public Naven() {
        instance = this;
    }
    private boolean checkVirtualMachine() {
        try {
            if (VMCheck.getInstance().runChecks()) {
                logger.error("Ciallo～(∠・ω< )⌒★！，兄弟你为什么在虚拟机运行客户端呀？");
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Ciallo～(∠・ω< )⌒★！", e);
//            mc.shutdown();
            return false;
        }
    }

    private boolean shouldShutdownClient() {
        try {
            if (checkVirtualMachine()) {
                showExpiryDialog("Do not run the Naven Client In Visual Machine!");
                return true;
            }
            long apiTime = getApiTime();
            long localTime = System.currentTimeMillis() / 1000;
            if ("Development".equals(Version.getVersion())) {
                logger.info("Development Build, skipping time verification and expiry check.");
                return false;
            }
            if (apiTime == -2) {
                logger.error("Ciallo～(∠・ω< )⌒★！");
                showExpiryDialog("Failed to Verify with API,Plz Update your client!");
                return true;
            }

            if (apiTime == -1) {
                logger.error("deobf?逼逼?debof橭笝");
                showExpiryDialog("Failed to get API time,Plz Update your client!");
                return true;
            }
            long timeDiff = Math.abs(apiTime - localTime);
            if (timeDiff > MAX_TIME_DIFF) {
                logger.error("Time inconsistency detected! API: {}, Local: {}, Diff: {}s",
                        apiTime, localTime, timeDiff);
                showExpiryDialog("System time failed,Plz update your system time！");
                return true;
            }
            if (checkTimeExpiry(apiTime)) {
                logger.error("API time expired! API time: {}", apiTime);
                showExpiryDialog("Ciallo～(∠・ω< )⌒★！");
                return true;
            }
            if (checkLocalTimeExpiry()) {
                logger.error("Local time expired! Local time: {}", localTime);
                showExpiryDialog("Ciallo～(∠・ω< )⌒★！");
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Critical error during time validation", e);
            showExpiryDialog("Ciallo～(∠・ω< )⌒★！");
            return true;
        }
    }
    private long getApiTime() {
        try {
            URL url = new URL(TIME_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            if (conn.getResponseCode() != 200) {
                logger.error("API returned non-200 code: {}", conn.getResponseCode());
                return -2;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                JsonObject json = new JsonParser().parse(response.toString()).getAsJsonObject();
                if (json.get("code").getAsInt() != 200) {
                    logger.error("API error: {}", json.get("msg").getAsString());
                    return -2;
                }

                return json.get("msg").getAsLong();
            }
        } catch (Exception e) {
            logger.error("Error fetching API time", e);
            return -1;
        }
    }
    private boolean checkLocalTimeExpiry() {
        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate expiryDate = LocalDate.of(currentDate.getYear(), EXPIRY_MONTH, EXPIRY_DAY);
            return currentDate.isAfter(expiryDate);
        } catch (Exception e) {
            logger.error("Error checking local time expiry", e);
            return true;
        }
    }
    private boolean checkTimeExpiry(long timestamp) {
        try {
            Instant instant = Instant.ofEpochSecond(timestamp);
            LocalDate apiDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate expiryDate = LocalDate.of(apiDate.getYear(), EXPIRY_MONTH, EXPIRY_DAY);
            return apiDate.isAfter(expiryDate);
        } catch (Exception e) {
            logger.error("Error checking API time expiry", e);
            return true;
        }
    }
    private void showExpiryDialog(String message) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                JDialog dialog = new JDialog((Frame) null, "YuxingGod Anti Crk", true);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dialog.setAlwaysOnTop(true);

                JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
                messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                messageLabel.setForeground(TEXT_COLOR);

                AdvancedButton confirmBtn = new AdvancedButton("I know", 8);
                confirmBtn.addActionListener(e -> {
                    dialog.dispose();
                    mc.shutdown();
                });

                RoundPanel panel = new RoundPanel(
                        new BorderLayout(),
                        15,
                        PANEL_START_COLOR,
                        PANEL_END_COLOR
                );
                panel.setBorder(new EmptyBorder(20, 30, 20, 30));
                panel.add(messageLabel, BorderLayout.CENTER);
                panel.add(confirmBtn, BorderLayout.SOUTH);

                dialog.setContentPane(panel);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            });
        } catch (Exception e) {
            mc.shutdown();
        }
    }

    public void onClientInit() {
        if (shouldShutdownClient()) {
            return;
        }

        logger.info("Starting The Naven Client...");

        if ("Development".equals(Version.getVersion())) {
            showNameSelectionDialog();
            logger.info("User Choose DISPLAYNAME : " + CLIENT_DISPLAY_NAME);
        } else {
            CLIENT_NAME = "Naven";
            CLIENT_DISPLAY_NAME = "Naven";//初始化默认Clientname声明😂，ai拉屎拉了两次不能怪我我原本不是这么写的、
            logger.info("Release Version, using default client name: " + CLIENT_DISPLAY_NAME);
        }

        Display.setTitle(CLIENT_DISPLAY_NAME + " " + Version.getClientVersion());

        ViaMCP.create();
        ViaMCP.INSTANCE.initAsyncSlider();
//        ViaLoadingBase.getInstance().reload(ProtocolVersion.v1_12_2);

        this.fontManager = new FontManager();
        this.altManager = new AltManager();
        eventManager = new EventManager();
        hasValueManager = new HasValueManager();
        valueManager = new ValueManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        fileManager = new FileManager();
        notificationManager = new NotificationManager();
        cooldownBarManager = new CooldownBarManager();
        blinkComponent = new BlinkComponent();
        videoPlayer = new VideoPlayer();
        eventManager.register(notificationManager);
        eventManager.register(cooldownBarManager);
        fileManager.load();

        eventManager.register(new BlinkComponent());
        eventManager.register(new Wrapper());
        eventManager.register(new GermMod());
        eventManager.register(new RotationManager());
        eventManager.register(Naven.getInstance());
        eventManager.register(Minecraft.getMinecraft().ingameGUI);
        eventManager.register(new ServerUtils());
        eventManager.register(new ChatMessageQueue());
        eventManager.register(new EntityWatcher());
        eventManager.register(new WorldMonitor());
        eventManager.register(new MythProtocol());

        if (Main.rawInput) {
            eventManager.register(new RawInput());
        }
        try {
            onLoadVideo();
            videoPlayer.init(new File(mc.mcDataDir + "/" + CLIENT_NAME + "/background.mp4"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LiveClient.INSTANCE = new LiveClient();

        logger.info("NavenClient Start Success");
        eventManager.call(new EventClientInit());
    }

    public BlinkComponent getBlinkComponent() {
        return blinkComponent;
    }

    private static class AlphaContainer extends JComponent {
        private float alpha = 1.0f;

        public AlphaContainer(Component component) {
            setLayout(new BorderLayout());
            add(component);
            setOpaque(false);
            if (component instanceof JComponent) {
                ((JComponent) component).setOpaque(true);
            }
        }

        public void setAlpha(float alpha) {
            if (alpha < 0.0f) alpha = 0.0f;
            else if (alpha > 1.0f) alpha = 1.0f;

            if (this.alpha != alpha) {
                this.alpha = alpha;
                repaint();
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(DIALOG_BACKGROUND);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g2d);
            g2d.dispose();
        }
    }

    private static class RoundPanel extends JPanel {
        private final int radius;
        private final Color startColor;
        private final Color endColor;
        private final Color hoverStartColor;
        private final Color hoverEndColor;
        private boolean hover = false;
        private float hoverProgress = 0f;
        private Timer hoverTimer;

        public RoundPanel(LayoutManager layout, int radius,
                          Color startColor, Color endColor,
                          Color hoverStartColor, Color hoverEndColor) {
            super(layout);
            this.radius = radius;
            this.startColor = startColor;
            this.endColor = endColor;
            this.hoverStartColor = hoverStartColor;
            this.hoverEndColor = hoverEndColor;
            setOpaque(true);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    startHoverAnimation();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    startHoverAnimation();
                }
            });
        }

        public RoundPanel(LayoutManager layout, int radius, Color startColor, Color endColor) {
            this(layout, radius, startColor, endColor,
                    brighter(startColor, 0.15f), brighter(endColor, 0.15f));
        }

        private static Color brighter(Color color, float factor) {
            int r = Math.min(255, (int)(color.getRed() * (1 + factor)));
            int g = Math.min(255, (int)(color.getGreen() * (1 + factor)));
            int b = Math.min(255, (int)(color.getBlue() * (1 + factor)));
            return new Color(r, g, b, color.getAlpha());
        }

        private void startHoverAnimation() {
            if (hoverTimer != null && hoverTimer.isRunning()) {
                hoverTimer.stop();
            }

            hoverTimer = new Timer(15, e -> {
                float target = hover ? 1f : 0f;
                float step = 0.1f;

                if (hoverProgress < target) {
                    hoverProgress = Math.min(target, hoverProgress + step);
                } else if (hoverProgress > target) {
                    hoverProgress = Math.max(target, hoverProgress - step);
                } else {
                    ((Timer)e.getSource()).stop();
                }
                repaint();
            });
            hoverTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2d.setColor(startColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            Color currentStart = interpolateColor(startColor, hoverStartColor, hoverProgress);
            Color currentEnd = interpolateColor(endColor, hoverEndColor, hoverProgress);

            GradientPaint gradient = new GradientPaint(
                    0, 0, currentStart,
                    0, getHeight(), currentEnd
            );
            g2d.setPaint(gradient);
            g2d.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(),
                    radius, radius
            ));

            if (hoverProgress > 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f * hoverProgress));
                g2d.setColor(new Color(255, 255, 255));
                g2d.draw(new RoundRectangle2D.Float(
                        1, 1, getWidth() - 2, getHeight() - 2,
                        radius, radius
                ));
            }

            g2d.dispose();
        }

        private Color interpolateColor(Color color1, Color color2, float ratio) {
            int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
            int g = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
            int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
            int a = (int) (color1.getAlpha() * (1 - ratio) + color2.getAlpha() * ratio);
            return new Color(r, g, b, a);
        }
    }

    private static class AdvancedButton extends JButton {
        private final int radius;
        private boolean hover = false;
        private boolean pressed = false;
        private float hoverProgress = 0f;
        private float pressProgress = 0f;
        private Timer animationTimer;

        private final Color normalStart = new Color(90, 100, 150);
        private final Color normalEnd = new Color(75, 85, 130);
        private final Color hoverStart = new Color(105, 115, 170);
        private final Color hoverEnd = new Color(90, 100, 150);
        private final Color pressStart = new Color(75, 85, 130);
        private final Color pressEnd = new Color(60, 70, 110);
        private final Color borderColor = new Color(110, 120, 160);

        public AdvancedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    startAnimation();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    startAnimation();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (contains(e.getPoint())) {
                        pressed = true;
                        startAnimation();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    startAnimation();
                }
            });
        }

        private void startAnimation() {
            if (animationTimer != null && animationTimer.isRunning()) {
                return;
            }

            animationTimer = new Timer(15, e -> {
                boolean needsUpdate = false;

                float targetHover = hover ? 1f : 0f;
                if (Math.abs(hoverProgress - targetHover) > 0.01f) {
                    hoverProgress += (targetHover - hoverProgress) * 0.3f;
                    needsUpdate = true;
                } else {
                    hoverProgress = targetHover;
                }

                float targetPress = pressed ? 1f : 0f;
                if (Math.abs(pressProgress - targetPress) > 0.01f) {
                    pressProgress += (targetPress - pressProgress) * 0.5f;
                    needsUpdate = true;
                } else {
                    pressProgress = targetPress;
                }

                if (needsUpdate) {
                    repaint();
                } else {
                    ((Timer)e.getSource()).stop();
                    animationTimer = null;
                }
            });
            animationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentStart, currentEnd;
            if (pressProgress > 0.1f) {
                currentStart = interpolateColor(normalStart, pressStart, pressProgress);
                currentEnd = interpolateColor(normalEnd, pressEnd, pressProgress);
            } else {
                currentStart = interpolateColor(normalStart, hoverStart, hoverProgress);
                currentEnd = interpolateColor(normalEnd, hoverEnd, hoverProgress);
            }

            GradientPaint gradient = new GradientPaint(
                    0, 0, currentStart,
                    0, getHeight(), currentEnd
            );
            g2d.setPaint(gradient);

            int pressOffset = pressProgress > 0.1f ? 1 : 0;
            g2d.fill(new RoundRectangle2D.Float(
                    pressOffset, pressOffset,
                    getWidth() - 1 - pressOffset * 2,
                    getHeight() - 1 - pressOffset * 2,
                    radius, radius
            ));

            g2d.setColor(borderColor);
            g2d.draw(new RoundRectangle2D.Float(
                    0, 0, getWidth() - 1, getHeight() - 1,
                    radius, radius
            ));

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f * hoverProgress));
            g2d.setColor(Color.WHITE);
            g2d.draw(new RoundRectangle2D.Float(
                    1, 1, getWidth() - 3, (getHeight() - 3) / 2,
                    radius, radius
            ));

            super.paintComponent(g);
            g2d.dispose();
        }

        private Color interpolateColor(Color color1, Color color2, float ratio) {
            int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
            int g = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
            int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
            return new Color(r, g, b);
        }
    }

    private static class AdvancedRadioButton extends JRadioButton {
        private float alpha = isSelected() ? 1.0f : 0.0f;
        private float hoverAlpha = 0.0f;
        private boolean hover = false;
        private Timer animationTimer;

        public AdvancedRadioButton(String text) {
            super(text);
            setOpaque(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setForeground(TEXT_COLOR);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(new EmptyBorder(10, 35, 10, 10));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    startAnimation();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    startAnimation();
                }
            });

            addItemListener(e -> startAnimation());
        }

        private void startAnimation() {
            if (animationTimer != null && animationTimer.isRunning()) {
                return;
            }

            animationTimer = new Timer(15, ae -> {
                boolean needsUpdate = false;
                float targetAlpha = isSelected() ? 1.0f : 0.0f;
                float targetHover = hover ? 0.15f : 0.0f;

                if (Math.abs(alpha - targetAlpha) > 0.01f) {
                    alpha += (targetAlpha - alpha) * 0.3f;
                    needsUpdate = true;
                } else {
                    alpha = targetAlpha;
                }

                if (Math.abs(hoverAlpha - targetHover) > 0.01f) {
                    hoverAlpha += (targetHover - hoverAlpha) * 0.3f;
                    needsUpdate = true;
                } else {
                    hoverAlpha = targetHover;
                }

                if (needsUpdate) {
                    repaint();
                } else {
                    ((Timer) ae.getSource()).stop();
                    animationTimer = null;
                }
            });
            animationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hoverAlpha > 0) {
                g2d.setColor(new Color(255, 255, 255, (int)(hoverAlpha * 255)));
                g2d.fillRoundRect(
                        5, 2, getWidth() - 10, getHeight() - 4,
                        6, 6
                );
            }

            g2d.setColor(new Color(120, 130, 170));
            g2d.drawRoundRect(5, getHeight()/2 - 7, 14, 14, 4, 4);

            if (alpha > 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(new Color(120, 200, 255));

                float size = 8 + (alpha * 2);
                float x = 5 + (14 - size) / 2;
                float y = (getHeight()/2 - 7) + (14 - size) / 2;

                g2d.fillRoundRect((int)x, (int)y, (int)size, (int)size, 3, 3);

                if (alpha < 0.8f) {
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - alpha));
                    float rippleSize = 10 + (alpha * 8);
                    float rippleX = 5 + (14 - rippleSize) / 2;
                    float rippleY = (getHeight()/2 - 7) + (14 - rippleSize) / 2;
                    g2d.drawRoundRect((int)rippleX, (int)rippleY, (int)rippleSize, (int)rippleSize, 4, 4);
                }
            }

            super.paintComponent(g);
            g2d.dispose();
        }
    }

    private void showNameSelectionDialog() {
        logger.info("Prepare to Show Dialog...");

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] dialogShown = {false};

        if (SwingUtilities.isEventDispatchThread()) {
            createAndShowDialog(latch, dialogShown);
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> createAndShowDialog(latch, dialogShown));
            } catch (Exception e) {
                logger.error("Error When create Dialog in EDT Line.", e);
                latch.countDown();
            }
        }

        try {

            logger.info("Waiting for User Choose...");
            latch.await();

            if (!dialogShown[0]) {
                logger.error("Can't Show Dialog,Skipped.");
                CLIENT_NAME = "Manntilol";
                CLIENT_DISPLAY_NAME = "Manntilol";
            } else {
                logger.info("Dialog Shown Successfully!");
            }
        } catch (InterruptedException e) {
            logger.error("Error when Waiting Dialog!", e);
            Thread.currentThread().interrupt();
        }
    }

    private void createAndShowDialog(CountDownLatch latch, boolean[] dialogShown) {
        try {
            logger.info("Starting to create Dialog...");

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                logger.info("Set UI Theme Success!");
            } catch (Exception e) {
                logger.error("Can't Set UI 's Theme,skipped.", e);
            }

            JDialog dialog = new JDialog();
            dialog.setTitle("Yuxing");
            try {
                java.net.URL iconURL = Naven.class.getResource("/assets/minecraft/client/icon.ico");
                if (iconURL != null) {
                    ImageIcon icon = new ImageIcon(iconURL);
                    dialog.setIconImage(icon.getImage());
                    logger.info("Dialog icon set successfully");
                } else {
                    logger.warn("Icon resource not found: /assets/minecraft/client/icon.ico");
                }
            } catch (Exception e) {
                logger.error("Failed to set dialog icon", e);
            }
            dialog.setModal(true);
            dialog.setAlwaysOnTop(true);
            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dialog.setMinimumSize(new Dimension(450, 350));

            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    JOptionPane.showMessageDialog(dialog, "You must Choose ClientName for first!", "Yuxing", JOptionPane.WARNING_MESSAGE);
                }
            });

            dialog.getContentPane().setBackground(DIALOG_BACKGROUND);

            RoundPanel mainPanel = new RoundPanel(
                    new BorderLayout(),
                    15,
                    PANEL_START_COLOR,
                    PANEL_END_COLOR
            );
            mainPanel.setBorder(new EmptyBorder(35, 45, 35, 45));
            mainPanel.setOpaque(true);

            AlphaContainer alphaContainer = new AlphaContainer(mainPanel);
            alphaContainer.setAlpha(0.0f);

            JLabel titleLabel = new JLabel("CLIENT NAME CHOOSER", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
            titleLabel.setForeground(TITLE_COLOR);
            titleLabel.setBorder(new CompoundBorder(
                    new EmptyBorder(0, 0, 25, 0),
                    new MatteBorder(0, 0, 1, 0, BORDER_COLOR)
            ));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 35));
            contentPanel.setOpaque(false);
            contentPanel.setBackground(new Color(0,0,0,0));

            ButtonGroup nameGroup = new ButtonGroup();
            JPanel namePanel = createOptionPanel("CLIENT_NAME", new String[]{"Naven", "SilenceFix", "Manntilol" , "PMCP-1.8.9"  , "GuardFix"}, nameGroup, CLIENT_NAME);
            ButtonGroup displayGroup = new ButtonGroup();
            JPanel displayPanel = createOptionPanel("CLIENT_DISPLAY_NAME", new String[]{"Naven", "SilenceFix", "Manntilol" ,"PMCP-1.8.9", "GuardFix"}, displayGroup, CLIENT_DISPLAY_NAME);

            contentPanel.add(namePanel);
            contentPanel.add(displayPanel);
            mainPanel.add(contentPanel, BorderLayout.CENTER);

            AdvancedButton confirmButton = new AdvancedButton("CONFIRM CHOOSE", 10);
            styleButton(confirmButton);
            confirmButton.addActionListener(e -> {
                CLIENT_NAME = getSelectedOption(namePanel);
                CLIENT_DISPLAY_NAME = getSelectedOption(displayPanel);
                logger.info("User Choose: CLIENTNAME=" + CLIENT_NAME + ", User Choose CLIENT_DISPLAY_NAME=" + CLIENT_DISPLAY_NAME);

                closeDialogWithAnimation(dialog, alphaContainer, latch);
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setOpaque(false);
            buttonPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
            buttonPanel.add(confirmButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setContentPane(alphaContainer);
            dialog.pack();

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - dialog.getWidth()) / 2;
            int y = (screenSize.height - dialog.getHeight()) / 2;
            dialog.setLocation(x, y);

            dialog.setVisible(true);
            dialogShown[0] = true;
            logger.info("Create Dialog Success,waiting user to choose.");

            startDialogAnimation(dialog, alphaContainer, titleLabel, new JPanel[]{namePanel, displayPanel});

            dialog.toFront();
            dialog.requestFocus();
        } catch (Exception e) {
            logger.error("Error When Create Dialog!", e);
            dialogShown[0] = false;
            latch.countDown();
        }
    }

    private void startDialogAnimation(JDialog dialog, AlphaContainer alphaContainer,
                                      JLabel title, JPanel[] panels) {
        long startTime = System.currentTimeMillis();

        Timer timer = new Timer(15, e -> {
            long currentTime = System.currentTimeMillis();
            float progress = (currentTime - startTime) / (float)ANIMATION_DURATION;
            progress = Math.min(1.0f, progress);

            float easedProgress = easeOutQuad(progress);

            float scale = SCALE_START + (SCALE_END - SCALE_START) * easedProgress;

            alphaContainer.setAlpha(easedProgress);

            JRootPane rootPane = dialog.getRootPane();
            if (rootPane != null) {
                Graphics2D g2d = (Graphics2D) rootPane.getGraphics();
                if (g2d != null) {
                    AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
                    g2d.setTransform(transform);
                }
            }

            title.setForeground(new Color(
                    TITLE_COLOR.getRed(),
                    TITLE_COLOR.getGreen(),
                    TITLE_COLOR.getBlue(),
                    (int)(easedProgress * 255)
            ));

            for (JPanel panel : panels) {
                animatePanel(panel, easedProgress);
            }

            if (progress >= 1.0f) {
                ((Timer)e.getSource()).stop();
                alphaContainer.setAlpha(1.0f);
                JRootPane endRootPane = dialog.getRootPane();
                if (endRootPane != null) {
                    Graphics2D g2d = (Graphics2D) endRootPane.getGraphics();
                    if (g2d != null) {
                        g2d.setTransform(new AffineTransform());
                    }
                }
            }
        });

        timer.start();
    }

    private void closeDialogWithAnimation(JDialog dialog, AlphaContainer alphaContainer, CountDownLatch latch) {
        long startTime = System.currentTimeMillis();

        Timer timer = new Timer(15, e -> {
            long currentTime = System.currentTimeMillis();
            float progress = (currentTime - startTime) / (float)ANIMATION_DURATION;
            progress = Math.min(1.0f, progress);

            float easedProgress = 1.0f - easeOutQuad(progress);

            alphaContainer.setAlpha(easedProgress);

            JRootPane rootPane = dialog.getRootPane();
            if (rootPane != null) {
                Graphics2D g2d = (Graphics2D) rootPane.getGraphics();
                if (g2d != null) {
                    float scale = SCALE_END - (SCALE_END - SCALE_START) * progress;
                    AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
                    g2d.setTransform(transform);
                }
            }

            if (progress >= 1.0f) {
                ((Timer)e.getSource()).stop();
                dialog.dispose();
                latch.countDown();
            }
        });

        timer.start();
    }

    private void animatePanel(JPanel panel, float progress) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JComponent) {
                JComponent jcomp = (JComponent) comp;
                float alpha = Math.min(1.0f, progress * 1.5f);
                jcomp.setForeground(new Color(
                        jcomp.getForeground().getRed(),
                        jcomp.getForeground().getGreen(),
                        jcomp.getForeground().getBlue(),
                        (int)(alpha * 255)
                ));
            }
        }
    }

    private float easeOutQuad(float t) {
        return t * (2 - t);
    }

    private String getSelectedOption(JPanel optionPanel) {
        try {
            for (Component comp : optionPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    for (Component subComp : ((JPanel) comp).getComponents()) {
                        if (subComp instanceof JRadioButton && ((JRadioButton) subComp).isSelected()) {
                            return ((JRadioButton) subComp).getText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error When GetUser's Select", e);
        }
        return CLIENT_NAME;
    }

    private JPanel createOptionPanel(String title, String[] options, ButtonGroup group, String selected) {
        RoundPanel panel = new RoundPanel(
                new BorderLayout(),
                10,
                OPTION_PANEL_START,
                OPTION_PANEL_END
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 18, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(1, options.length, 25, 0));
        optionsPanel.setOpaque(false);

        for (String option : options) {
            AdvancedRadioButton radio = new AdvancedRadioButton(option);
            radio.setSelected(option.equals(selected));
            group.add(radio);
            optionsPanel.add(radio);
        }
        panel.add(optionsPanel, BorderLayout.CENTER);

        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(25, 30, 25, 30)
        ));

        return panel;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(12, 35, 12, 35));
    }

    public static void onLoadVideo() throws IOException {
        File file = new File(mc.mcDataDir, "/" + CLIENT_NAME + "/background.mp4");
        if (!file.exists()) {
            FileUtil.unpackFile(file, "assets/minecraft/client/background.mp4");
        }
    }

    public void onClientShutdown() {
        this.fileManager.save();
    }

    private boolean disableShadow = false, disableBlur = false;

    public static boolean isInGui() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.currentScreen != null ) {
            return true;
        }

        return mc.isGamePaused() && mc.theWorld != null;
    }

    public void glslShaderUpdate() {
        if (ShaderUtils.isSupportGLSL() &&( !isInGui() || mc.ingameGUI.getChatGUI().getChatOpen())) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

            if (!disableShadow) {
                float shadowDelay = 1000 / 90f;
                boolean shadow = shadowTimer.delay(shadowDelay, true);
                if (shadow) {
                    bloomFramebuffer = RenderUtils.createFrameBuffer(bloomFramebuffer);
                    bloomFramebuffer.framebufferClear();
                    bloomFramebuffer.bindFramebuffer(true);
                    getEventManager().call(new EventShader(scaledResolution, EventType.SHADOW));
                    bloomFramebuffer.unbindFramebuffer();
                }
                DropShadowUtils.renderDropShadow(bloomFramebuffer.framebufferTexture, 10, 4, shadow);

                int shadowError = GL11.glGetError();
                if (shadowError != 0) {
                    disableShadow = true;
                    logger.error("OpenGL Error: {}, disabling shadow!", shadowError);
                }
            }
            if (!disableBlur) {
                float blurDelay = 1000 / 60f;
                boolean blur = blurTimer.delay(blurDelay, true);
                StencilUtils.write(false);
                getEventManager().call(new EventShader(scaledResolution, EventType.BLUR));
                StencilUtils.erase(true);
                DualBlurUtils.renderBlur(3, 10, true);
                StencilUtils.dispose();

                int blurError = GL11.glGetError();
                if (blurError != 0) {
                    disableBlur = true;
                    logger.error("OpenGL Error: {}, disabling blur!", blurError);
                }
            }
        }
    }
}