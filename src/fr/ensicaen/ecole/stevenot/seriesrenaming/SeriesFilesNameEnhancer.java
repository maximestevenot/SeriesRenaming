package fr.ensicaen.ecole.stevenot.seriesrenaming;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SeriesFilesNameEnhancer {

    private final String filesExtension;
    private final String seasonPrefix;
    private File[] files;
    private List<String> names;
    private File contentFolder;


    public SeriesFilesNameEnhancer(String folderPath, String filesExtension, int seasonNumber, String namesFilePath) throws SeriesRenamingException {

        contentFolder = new File(folderPath);
        seasonPrefix = "S" + String.format("%02d", seasonNumber) + "E";

        if (!contentFolder.isDirectory()) {
            throw new SeriesRenamingException(folderPath + " directory not found");
        }

        this.filesExtension = getCleanFilesExtension(filesExtension);

        files = getFiles(contentFolder, this.filesExtension);
        if (files.length == 0) {
            throw new SeriesRenamingException("No " + this.filesExtension + " file found in " + folderPath);
        }

        if (namesFilePath == null) {
            return;
        }
        names = getNames(namesFilePath);
        if (names == null || names.isEmpty() || names.size() != files.length) {
            throw new SeriesRenamingException(namesFilePath + " content doesn't match with the folder content");
        }
    }

    public SeriesFilesNameEnhancer(String folderPath, String filesExtension, int seasonNumber) throws SeriesRenamingException {
        this(folderPath, filesExtension, seasonNumber, null);

    }

    public void applyRenaming() throws SeriesRenamingException {

        if (names == null) {
            throw new SeriesRenamingException("An error occurred");
        }

        for (int i = 0; i < files.length; i++) {

            String destinationPath = contentFolder.getAbsolutePath() + "/";
            destinationPath += seasonPrefix + String.format("%02d", i + 1) + " - ";
            destinationPath += names.get(i).trim() + filesExtension;

            try {
                Files.move(files[i].toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new SeriesRenamingException("Error while renaming " + files[i].getAbsolutePath() + " into " + destinationPath);
            }
        }
    }

    public void addSeasonNumber() throws SeriesRenamingException {

        for (int i = 0; i < files.length; i++) {
            String path = files[i].getAbsolutePath();
            int separatorLastIndex = Math.max(path.lastIndexOf('/'), path.lastIndexOf("\\"));
            if (separatorLastIndex <= 0) {
                return;
            }

            String destinationPath = path.substring(0, separatorLastIndex + 1) + seasonPrefix + path.substring(separatorLastIndex + 1);
            try {
                Files.move(files[i].toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new SeriesRenamingException("Error while adding season number for " + files[i].getAbsolutePath() + "   "+destinationPath);
            }

        }
    }

    private List<String> getNames(String namesFilePath) throws SeriesRenamingException {

        File namesFile = new File(namesFilePath);
        if (!namesFile.exists()) {
            throw new SeriesRenamingException(namesFilePath + " not found");
        }

        List<String> lines;

        try {
            lines = Files.readAllLines(namesFile.toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new SeriesRenamingException("Error while reading " + namesFilePath, e);
        }

        return lines;
    }

    private File[] getFiles(File directory, final String filesExtension) {
        return directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir1, String name) {
                return name.toLowerCase().endsWith(filesExtension);
            }
        });
    }

    private String getCleanFilesExtension(String filesExtension) {
        filesExtension = filesExtension.trim().toLowerCase();
        if (!filesExtension.startsWith(".")) {
            filesExtension = "." + filesExtension;
        }
        return filesExtension;
    }
}
