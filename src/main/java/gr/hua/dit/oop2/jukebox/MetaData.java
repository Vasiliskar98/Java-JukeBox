package gr.hua.dit.oop2.jukebox;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.id3.ID3v22Tag;
public class MetaData {

    // Return meta-data list
    public ArrayList<String> getMetaData(String path) {
        try {
            MP3File audioFile = (MP3File) AudioFileIO.read(new File(path));
            Tag tag = audioFile.getTag();
            if (tag == null) {
                tag = new ID3v22Tag();
            }

            ArrayList<String> tagList = new ArrayList<String>();
            tagList.add(tag.getFirst(FieldKey.ARTIST));
            tagList.add(tag.getFirst(FieldKey.ALBUM));
            tagList.add(tag.getFirst(FieldKey.GENRE));
            tagList.add(tag.getFirst(FieldKey.LANGUAGE));
            tagList.add(tag.getFirst(FieldKey.YEAR));
            tagList.add(tag.getFirst(FieldKey.RATING));
            return tagList;

        } catch (CannotReadException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        } catch (InvalidAudioFrameException e) {
            throw new RuntimeException(e);
        } catch (ReadOnlyFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // Return true if the song has meta-data, otherwise false
    public boolean hasMetaData(String path) {
        try {
            MP3File audioFile = (MP3File) AudioFileIO.read(new File(path));
            if (audioFile.hasID3v2Tag() || audioFile.hasID3v1Tag()) {
                return true;
            } else {
                return false;
            }
        } catch (CannotReadException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        } catch (InvalidAudioFrameException e) {
            throw new RuntimeException(e);
        } catch (ReadOnlyFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

