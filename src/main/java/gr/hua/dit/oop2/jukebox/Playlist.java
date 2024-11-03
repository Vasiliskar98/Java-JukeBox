package gr.hua.dit.oop2.jukebox;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Playlist {

    private static Logger logger = Logger.getLogger("Playlist");
    private File[] fileList;
    private String fileName;
    private String songName, st, name, absolutePath;
    private ArrayList<Song> playlist;
    private ArrayList<String> songNames = new ArrayList<String>();
    private String songData;

    public void createPlaylist(File file){
        logger.setLevel(Level.ALL);
        playlist = new ArrayList<Song>();
        playlist.removeAll(playlist);
        fileName = file.getName();
        // Searches the given directory and all its subdirectories for '.mp3' files
        if (file.isDirectory()) {
            findAllMp3(file);
        } else {
            if (fileName.endsWith(".m3u") && file.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String parentDirectory = file.getParent();
                    System.out.print(parentDirectory);
                    while ((st = br.readLine()) != null) {
                        absolutePath = "";
                        // Find songs absolute path
                        if (st.startsWith("#") || st.isBlank()) {
                            continue;
                        }
                        if (st.startsWith("/")) {
                            absolutePath = st;
                        } else {
                            absolutePath = parentDirectory + "/" + st;
                        }
                        File song = new File(absolutePath);
                        if (song.exists()) {
                            // Store the song in a list and set metadata
                            Song s = new Song(absolutePath, getNameFromPath(absolutePath));
                            playlist.add(s);
                            setMetaData(s);
                        } else {
                            logger.log(Level.INFO, "Unacceptable file: " + absolutePath);
                        }
                    }
                    br.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "IOException occurred: ", e);
                }
            } else if (fileName.endsWith(".mp3") && file.exists()) {
                Song s = new Song(file.getAbsolutePath(), fileName);
                playlist.add(s);
                setMetaData(s);
            } else {
                logger.log(Level.INFO, "Unacceptable file: " + fileName);
            }
        }
    }

    public void findAllMp3(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                findAllMp3(f);
            } else {
                if (f.getName().endsWith(".mp3")) {
                    Song s = new Song(f.getAbsolutePath(), f.getName());
                    playlist.add(s);
                    setMetaData(s);
                } else {
                    logger.log(Level.INFO, "Unacceptable file: " + f.getName());
                }
            }
        }
    }

    //Return a list with song names
    public ArrayList<String> getSongNames(){
        for (Song s : playlist) {
            name = getNameFromPath(s.getPath());
            s.setName(name);
            songNames.add(name);
        }
        return songNames;
    }

    // Helper Functions
    public Song getSong(int position) {
        return playlist.get(position);
    }

    public void clear() {
        playlist.removeAll(playlist);
    }

    public int getSize() {
        return playlist.size();
    }

    public boolean hasNext(int position) {
        if (position == playlist.size() - 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isEmpty() {
        if (playlist.isEmpty()) {
            return true;
        }
        return false;
    }

    public String getNameFromPath(String path) {
        return path.substring(path.lastIndexOf("/")+1);
    }

    // Return song's meta-data list
    public String getSongData(Song s) {
        if (s.isData()) {
            songData = "";
            if (!s.getArtist().equals(null) && !s.getArtist().equals("")) {
                songData = songData + "Artist: " + s.getArtist() + " | ";
            }
            if (!s.getAlbum().equals(null) && !s.getAlbum().equals("")) {
                songData = songData + "Album: " + s.getAlbum() + " | ";
            }
            if (!s.getGenre().equals(null) && !s.getGenre().equals("")) {
                songData = songData + "Genre: " + s.getGenre() + " | ";
            }
            if (!s.getYear().equals(null) && !s.getYear().equals("")) {
                songData = songData + "Year: " + s.getYear() + " | ";
            }
            if (!s.getRating().equals(null) && !s.getRating().equals("")) {
                songData = songData + "Rating: " + s.getRating() + " | ";
            }
            if (!s.getLanguage().equals(null) && !s.getLanguage().equals("")) {
                songData = songData + "Language: " + s.getAlbum() + " | ";
            }
        } else {
            return "";
        }
        return  songData;
    }

    // Set the meta-data on the song's fields
    public void setMetaData(Song song) {
        MetaData md = new MetaData();
        if (md.hasMetaData(song.getPath())) {
            song.setData(true);
            ArrayList<String> metaDataList = md.getMetaData(song.getPath());
            song.setArtist(metaDataList.get(0));
            song.setAlbum(metaDataList.get(1));
            song.setGenre(metaDataList.get(2));
            song.setLanguage(metaDataList.get(3));
            song.setYear(metaDataList.get(4));
            song.setRating(metaDataList.get(5));
        } else {
            song.setData(false);
        }
    }
}
