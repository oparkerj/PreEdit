package com.ssplugins.preedit.launcher;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Launcher {
    
    public static void main(String[] args) {
        Launcher launcher = new Launcher(args);
        Optional<Version> op = launcher.detectLatestVersion();
        if (!op.isPresent()) {
            downloadAndLaunch(launcher, false, 0);
        }
        else {
            try {
                Settings settings = new Settings(new File(getApplicationDirectory(), "settings.json"));
                Version latest = launcher.getLatestVersion();
                if (op.get().compareTo(latest) < 0) {
                    if (settings.getAutoUpdate()) {
                        downloadAndLaunch(launcher, true, settings.getKeepVersions());
                        return;
                    }
                    if (settings.getSkipVersion().equals(latest.toString())) {
                        launchVersion(launcher, op.get());
                        return;
                    }
                    else {
                        settings.removeSkipVersion();
                    }
                    String[] options = new String[] {"Yes", "No", "Skip Version", "Always Update"};
                    int i = JOptionPane.showOptionDialog(null,
                                                         "A new version (" + latest.toString() + ") is available. " +
                                                                 "Would you like to update?\nCurrent: " + op.get().toString(),
                                                         null, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                                                         null, options, options[0]);
                    if (i == 0 || i == 3) {
                        if (i == 3) {
                            settings.setAutoUpdate(true);
                            settings.removeSkipVersion();
                        }
                        downloadAndLaunch(launcher, true, settings.getKeepVersions());
                    }
                    else if (i == 1 || i == 2) {
                        if (i == 2) {
                            settings.setSkipVersion(latest);
                        }
                        launchVersion(launcher, op.get());
                    }
                    settings.save();
                    return;
                }
            } catch (UnirestException | IOException e) {
                launcher.log(e);
            }
            launchVersion(launcher, op.get());
        }
    }
    
    private static void thread(Runnable runnable) {
        new Thread(runnable).start();
    }
    
    private static void launchVersion(Launcher launcher, Version version) {
        try {
            launcher.launch(version);
        } catch (ClassNotFoundException | IOException e) {
            launcher.log(e);
            JOptionPane.showMessageDialog(null, "Unable to load the application. Show the 'errors.log' file to the developer if necessary.");
        }
    }
    
    private static void downloadAndLaunch(Launcher launcher, boolean msg, int keep) {
        try {
            Update update = launcher.downloadLatestVersion();
            String message = (msg ? update.getMessage() : null);
            launcher.launch(update.getVersion(), message);
            if (keep > 0) {
                List<Version> versions = launcher.detectVersions();
                if (versions.size() <= keep) {
                    return;
                }
                versions.stream().sorted().limit(versions.size() - keep).map(launcher::getVersionFile).forEach(File::delete);
            }
        } catch (IOException e) {
            launcher.log(e);
            JOptionPane.showMessageDialog(null, "There was a problem while trying to download the file.");
        } catch (UnirestException e) {
            launcher.log(e);
            JOptionPane.showMessageDialog(null, "Unable to download the latest version of the file.");
        } catch (ClassNotFoundException e) {
            launcher.log(e);
            JOptionPane.showMessageDialog(null, "Unable to load the application.");
        }
    }
    
    private static final String BASE_URL = "http://ssplugins.com/preedit/builds/";
    private static final String LIB_PREFIX = "PreEdit-build-";
    
    private String[] args;
    private File dir;
    
    public Launcher(String[] args) {
        this.args = args;
        this.dir = getApplicationDirectory();
        Unirest.setDefaultHeader("User-Agent", "PreEdit-Launcher (1.0.0)");
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            log(e);
            JOptionPane.showMessageDialog(null, "An error occurred. If you can, describe the issue to the developer and send the 'errors.log' file.");
        });
    }
    
    public static File getApplicationDirectory() {
        try {
            return new File(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            return new File(".");
        }
    }
    
    @SuppressWarnings("Duplicates")
    public void log(Throwable throwable) {
        throwable.printStackTrace();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(dir, "errors.log"), true)))) {
            throwable.printStackTrace(out);
            out.println();
            out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Version getLatestVersion() throws UnirestException {
        String version = Unirest.get(BASE_URL + "version.txt").asString().getBody();
        return new Version(version);
    }
    
    public Optional<String> getUpdateMessage(Version version) {
        try {
            String msg = Unirest.get(BASE_URL + "versions/" + version.toString() + ".txt").asString().getBody();
            return Optional.of(msg);
        } catch (UnirestException ignored) {}
        return Optional.empty();
    }
    
    public List<Version> detectVersions() {
        File libs = new File(dir, "libs");
        if (!libs.exists()) {
            boolean mkdir = libs.mkdir();
            if (!mkdir) {
                JOptionPane.showMessageDialog(null, "Unable to create 'libs' folder.");
                return null;
            }
            return Collections.emptyList();
        }
        String[] files = libs.list();
        if (files == null) {
            return Collections.emptyList();
        }
        List<Version> versions = new ArrayList<>();
        Pattern pattern = Pattern.compile(LIB_PREFIX + "(.+)\\.jar");
        for (String file : files) {
            Matcher m = pattern.matcher(file);
            if (m.find()) {
                versions.add(new Version(m.group(1)));
            }
        }
        return versions;
    }
    
    public Optional<Version> detectLatestVersion() {
        List<Version> versions = detectVersions();
        if (versions == null) return Optional.empty();
        return versions.stream().max(Version::compareTo);
    }
    
    public File getVersionFile(Version version) {
        File libs = new File(dir, "libs");
        return new File(libs, LIB_PREFIX + version.toString() + ".jar");
    }
    
    public boolean hasVersion(Version version) {
        return getVersionFile(version).exists();
    }
    
    public void launch(Version version) throws IOException, ClassNotFoundException {
        launch(version, null);
    }
    
    public void launch(Version version, String msg) throws IOException, ClassNotFoundException {
        if (!hasVersion(version)) {
            throw new IllegalArgumentException();
        }
        List<String> append = new ArrayList<>();
        append.add("wd:" + dir.getPath());
        if (msg != null) {
            append.add("msg:" + msg);
        }
        append.addAll(Arrays.asList(args));
        args = append.toArray(new String[0]);
        File file = getVersionFile(version);
        Runtime.getRuntime().exec("java -jar " + file.getPath() + " " + String.join(" ", args));
    }
    
    public Update downloadLatestVersion() throws IOException, UnirestException {
        Version version = getLatestVersion();
        try {
            InputStream in = Unirest.get(BASE_URL + "latest.php").asBinary().getBody();
            Files.copy(in, Paths.get(getVersionFile(version).toURI()), StandardCopyOption.REPLACE_EXISTING);
        } catch (MalformedURLException e) {
            log(e);
        }
        Optional<String> msg = getUpdateMessage(version);
        return new Update(version, msg.orElse(null));
    }
    
}
