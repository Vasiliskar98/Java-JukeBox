package gr.hua.dit.oop2.jukebox;


import gr.hua.dit.oop2.musicplayer.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI implements ActionListener, PlayerListener {

    private static  Logger logger = Logger.getLogger("GUI");
    private JFrame frame;
    private JPanel filePanel, playlistPanel, controlPanel;
    private JButton open, play, next, pause, resume, stop;;
    private JList songList;
    private JLabel infoLabel;
    private JComboBox<String> cb;
    private String[] strategies = {"Normal", "Order", "Loop", "Random"};

    private int returnVal;
    private File f;
    private JFileChooser fc;

    private Playlist playlist;
    private Strategy strategy;
    private Player p = PlayerFactory.getPlayer();

    private String songName, metaData;
    private int songPosition, nextSong;

    public void createAndShowGUI(){

        logger.setLevel(Level.ALL);

        frame = new JFrame();
        frame.setTitle("JukeBox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,400);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(540,200));

        filePanel = new JPanel();
        playlistPanel = new JPanel();
        controlPanel = new JPanel();

        open = new JButton("Open");
        open.setFocusPainted(false);
        open.addMouseListener(new Highlighter(open, Color.blue));
        infoLabel = new JLabel("Open File", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        filePanel.setLayout(new GridLayout(2,1));
        filePanel.add(infoLabel);
        filePanel.add(open);

        playlistPanel.setLayout(new BoxLayout(playlistPanel, BoxLayout.PAGE_AXIS));

        cb = new JComboBox(strategies);
        cb.setOpaque(false);
        cb.setBackground(Color.white);
        play = new JButton("Play");
        play.setFocusPainted(false);
        play.addMouseListener(new Highlighter(play, Color.blue));
        next = new JButton("Next");
        next.setFocusPainted(false);
        next.addMouseListener(new Highlighter(next, Color.blue));
        pause = new JButton("Pause");
        pause.setFocusPainted(false);
        pause.addMouseListener(new Highlighter(pause, Color.blue));
        resume = new JButton("Resume");
        resume.setFocusPainted(false);
        resume.addMouseListener(new Highlighter(resume, Color.blue));
        stop = new JButton("Stop");
        stop.setFocusPainted(false);
        stop.addMouseListener(new Highlighter(stop, Color.blue));
        controlPanel.setLayout(new GridLayout(1,6));
        controlPanel.add(play);
        controlPanel.add(next);
        controlPanel.add(pause);
        controlPanel.add(resume);
        controlPanel.add(stop);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(playlistPanel, BorderLayout.CENTER);
        frame.add(filePanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    public void addListeners(){
        // Add Action listeners, PlayerListener and WindowAdapter
        open.addActionListener(this);
        play.addActionListener(this);
        next.addActionListener(this);
        pause.addActionListener(this);
        resume.addActionListener(this);
        stop.addActionListener(this);
        cb.addActionListener(this);
        p.addPlayerListener(this);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                p.close();
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        // Set actions to be taken on specific events (open, play, next, pause, resume, stop, cb)
        if (actionEvent.getSource().equals(open)) {
            fc = new JFileChooser();
            fc.setDialogTitle("Open");
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fc.setAcceptAllFileFilterUsed(true);
            // Filters: '.m3u' files, '.mp3' files and 'All files', allowing the user to select only the desired file type.
            fc.setFileFilter(new FileNameExtensionFilter("Mp3 file", "mp3"));
            fc.setFileFilter(new FileNameExtensionFilter("M3u list", "m3u"));
            returnVal = fc.showOpenDialog(open);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (strategy != null) {
                    controlPanel.remove(cb);
                    p.stop();
                }
                f = fc.getSelectedFile();
                if (f != null) {
                    showList(f);
                }
            }
        } else if (actionEvent.getSource().equals(play)) {
            // If the song list is null, the user cannot use the player interface
            if (songList != null) {
                if (strategy.getStrategy().equals("Random")) {
                    nextSong = strategy.nextSong(playlist, songList.getSelectedIndex());
                    songList.setSelectedIndex(nextSong);
                    playSong(nextSong);
                } else {
                    playSong(songList.getSelectedIndex());
                }
            }
        } else if (actionEvent.getSource().equals(next)) {
            if (songList != null) {
               if (strategy.getStrategy().equals("Normal") || p.getStatus().toString().equals("IDLE")) {
                   nextSong = strategy.nextSong(playlist, songList.getSelectedIndex());
                   songList.setSelectedIndex(nextSong);
                   playSong(nextSong);
               } else {
                   p.stop();
               }
            }
        } else if (actionEvent.getSource().equals(pause)) {
            if (p.getStatus() == Player.Status.PLAYING) {
                songList.setSelectedIndex(songPosition);
                p.pause();
                infoLabel.setText("<html>Pause song: <font color='blue'>" + songName + "</font></html>");
            }
        } else if (actionEvent.getSource().equals(resume)) {
            if (p.getStatus() == Player.Status.PAUSED) {
                songList.setSelectedIndex(songPosition);
                p.resume();
                infoLabel.setText("<html>Resume song: <font color='blue'>" + songName + "</font></html>");

            }
        } else if (actionEvent.getSource().equals(stop)) {
            // Set strategy to Normal
            if (p.getStatus() != Player.Status.IDLE) {
                songList.setSelectedIndex(songPosition);
                p.stop();
                strategy.setStrategy("Normal");
                cb.setSelectedItem("Normal");
            }
        } else if (actionEvent.getSource().equals(cb)) {
            strategy.setStrategy(cb.getSelectedItem().toString());
        }
    }

    // Create a list of song names and add it to the playlist panel
    public void showList(File file) {
        if (playlist != null) {
            playlist.clear();
            playlistPanel.removeAll();
            playlistPanel.repaint();
        }
        infoLabel.setForeground(Color.black);
        playlist = new Playlist();
        playlist.createPlaylist(file);
        // If the file is not acceptable, print an error message
        if (playlist.isEmpty()) {
            infoLabel.setOpaque(false);
            infoLabel.setForeground(Color.red);
            if (file.isDirectory()) {
                infoLabel.setText("Folder '" + file.getName() + "' does not contain mp3 files");
            } else {
                infoLabel.setText("File '" + file.getName() + "' is not m3u list");
            }
        } else {
            infoLabel.setOpaque(true);
            infoLabel.setBackground(Color.lightGray);
            infoLabel.setText("<html>Open: <font color='blue'>" + file.getName() + "</font>" + "<span>, Strategy: <font color='blue'>" + cb.getSelectedItem().toString() + "</font></html>");
            // The list is not empty - create new JList
            songList = new JList();
            songList.setListData(playlist.getSongNames().toArray());
            playlistPanel.add(songList);
            controlPanel.add(cb);
            strategy = new Strategy(cb.getSelectedItem().toString());
            JScrollPane sp = new JScrollPane(songList);
            sp.setBorder(BorderFactory.createEmptyBorder());
            playlistPanel.add(sp);
            songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            songList.setSelectedIndex(0);
            songList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {
                    // If the song has meta-data, print them. Else, print just the name
                    metaData = playlist.getSongData(playlist.getSong(songList.getSelectedIndex()));
                    if (metaData.equals("")) {
                        infoLabel.setText("<html>Selected song: <font color='blue'>" + songList.getSelectedValue().toString() + "</font></html>");
                    } else {
                        infoLabel.setText("<html><font color='blue'>" + songList.getSelectedValue().toString() + "</font> | " + metaData + "</html>");
                    }
                    metaData = "";
                }
            });
            // Call the 'playSong' function on double-click and set the strategy to 'Normal'
            songList.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        strategy.setStrategy("Normal");
                        cb.setSelectedItem("Normal");
                        playSong(songList.getSelectedIndex());
                    }
                }
            });
        }
    }

    // Start playing the song at the position index in the playlist
    public void playSong(int index) {
        try {
            if (p.getStatus() != Player.Status.IDLE) {
                p.stop();
            }
            InputStream song = new FileInputStream(playlist.getSong(index).getPath());
            songName = playlist.getSong(index).getName();
            songPosition = songList.getSelectedIndex();
            p.startPlaying(song);
            infoLabel.setText("<html>Current Playing: <font color='blue'>" + songName + "</font>" + "<span>, Strategy: <font color='blue'>" + cb.getSelectedItem().toString() + "</font></html>");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "File not found: " + e);
        } catch (PlayerException pe) {
            logger.log(Level.SEVERE, "There is something wrong with the player: " + pe);
        }
    }

    // When the status is IDLE, play the next song using the current strategy
    @Override
    public void statusUpdated(PlayerEvent playerEvent) {
        logger.log(Level.INFO, "Status Changed to: " + playerEvent.getStatus());
        if (playerEvent.getStatus() == Player.Status.IDLE) {
            if (strategy.getStrategy().equals("Normal") && returnVal == 0) {
                infoLabel.setText("<html>Selected song: <font color='blue'>" + songList.getSelectedValue().toString() + "</font></html>");
            } else {
                nextSong = strategy.nextSong(playlist, songList.getSelectedIndex());
                if (nextSong < 0) {
                    songList.setSelectedIndex(0);
                    infoLabel.setText("List is over, open new list or play again");
                } else {
                    songList.setSelectedIndex(nextSong);
                    playSong(nextSong);
                }
            }
        }
    }
}
