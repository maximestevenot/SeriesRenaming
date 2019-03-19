package fr.ensicaen.ecole.stevenot.seriesrenaming;

public class Main {

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Insufficient number of arguments.");
            System.err.println("Correct usage: folderPath fileExtension seasonNumber [namesFilePath (default folderPath/names.txt)]");
            System.exit(1);
        }

        int shift = args[0].equalsIgnoreCase("-s") ? 1 : 0;

        String folderPath = args[0 + shift];
        String fileExtension = args[1 + shift];
        String namesFilePath = folderPath + "/names.txt";
        int seasonNumber = Integer.parseInt(args[2 + shift]);

        if (args.length >= 4 + shift) {
            namesFilePath = args[3 + shift];
        }

        try {
            switch (shift) {
                case 0:
                    new SeriesFilesNameEnhancer(folderPath, fileExtension, seasonNumber, namesFilePath).applyRenaming();
                    break;
                case 1:
                    new SeriesFilesNameEnhancer(folderPath, fileExtension, seasonNumber).addSeasonNumber();
                    break;
                default:
                    System.err.println("An error occurred");
                    System.exit(1);
            }

        } catch (SeriesRenamingException e) {
            System.err.println("Error occurred: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Process complete");
    }
}
