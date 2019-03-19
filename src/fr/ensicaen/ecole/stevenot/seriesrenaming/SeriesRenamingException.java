package fr.ensicaen.ecole.stevenot.seriesrenaming;

public class SeriesRenamingException extends Exception {

    public SeriesRenamingException(String message) {
        super(message);
    }

    public SeriesRenamingException(String message, Exception ex) {
        super(message, ex);
    }
}
