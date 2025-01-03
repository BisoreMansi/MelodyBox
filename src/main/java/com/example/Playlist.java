package com.example;




// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

class Playlists {
    Scanner scanner = new Scanner(System.in);
    Connection connection;
    User loggedInUser;
    boolean loggedIn = true;

    // Constructor to initialize the connection and user
    public Playlists(User loggedInUser) {
        this.connection = ConnectionClassForDM.getConnection(); // Get connection from the connection class
        this.loggedInUser = loggedInUser; // Pass the logged-in user
    }

 
            public void userMenu() {
                while (loggedIn) {
                    System.out.println("\nUser Menu:");
                    System.out.println("1. Play A Song");
                    System.out.println("2. Create Playlist");
                    System.out.println("3. View All Playlists");
                    System.out.println("4. Add Song to Playlist");
                    System.out.println("5. View Songs in Playlist");
                    System.out.println("6. Play Song From Playlist");
                    System.out.println("7. View All Album");
                    System.out.println("8. View Album Songs");
                    System.out.println("9. Play Album Songs");
                    System.out.println("10. Logout");
                    System.out.print("Choose an option: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    Album ad = new Album();

                    switch (choice) {
                        case 1:
                        
                        try{
                        String qry = "Select SongID, Title, Path from AllSongs";
                        PreparedStatement pst = connection.prepareStatement(qry);
                        ResultSet rs = pst.executeQuery();
                        while(rs.next()){
                            System.out.println(rs.getString("Title"));
                          //  list.add(rs.getString("Path"));
                        }
                        System.out.print("Select a song name from the following song list: ");
                            String name = scanner.nextLine();
                            PlaySong.playSong(name);
                        }catch(Exception e){
                            System.out.println("Couldn't find the songs!");
                        }
                        break;
        
                        case 2:
                            createPlaylist();
                            break;
                        case 3:
                            viewPlaylists();
                            break;
                        case 4:
                            addSongToPlaylist();
                            break;
                        case 5:
                            viewSongsInPlaylist();
                            break;
                        case 6:
                            playSongInPlaylist();
                            break;
                        case 7: // view Album
                            ad.viewAllAlbum();
                            break;
                        case 8: // view album songs
                            ad.viewAlbumSongs();
                            break;
                        case 9:     // play songs from album
                            ad.playAlbumSong();
                            break;
                        case 10:
                            loggedIn = false;
                             System.out.println("Logged out successfully.");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
        }
    }

    private void createPlaylist() {
        System.out.print("Enter playlist name: ");
        String playlistName = scanner.nextLine();

        try {
            String sql = "INSERT INTO Playlists (PlaylistName, UserID) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, playlistName);
            statement.setInt(2, loggedInUser.getUserID()); // Use the logged-in user's ID
            statement.executeUpdate();
            System.out.println("Playlist created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating playlist.");
            e.printStackTrace();
        }
    }

    private void viewPlaylists() {
        try {
            String sql = "SELECT * FROM Playlists WHERE UserID = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, loggedInUser.getUserID()); // Use the logged-in user's ID
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int playlistId = resultSet.getInt("PlaylistID");
                String name = resultSet.getString("PlaylistName");
                System.out.println("Playlist ID: " + playlistId + ", Name: " + name);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching playlists.");
            e.printStackTrace();
        }
    }

    private void addSongToPlaylist() {
        System.out.print("Enter playlist Name: ");
        String playlistName = scanner.nextLine();
        System.out.print("Enter song name: ");
        String title = scanner.nextLine();

        try {
            // Get PlaylistID from playlist table
            String qur = "SELECT PlaylistID FROM Playlists WHERE PlaylistName = ?";
            PreparedStatement statement = connection.prepareStatement(qur);
            statement.setString(1, playlistName);
            ResultSet rs = statement.executeQuery();
            int playlistID = 0;
            if (rs.next()) {
                playlistID = rs.getInt("PlaylistID");
            }

            // Get SongID from allsongs table
            String qur1 = "SELECT SongID FROM allsongs WHERE Title = ?";
            statement = connection.prepareStatement(qur1);
            statement.setString(1, title);
            rs = statement.executeQuery();
            int songId = 0;
            if (rs.next()) {
                songId = rs.getInt("SongID");
            }

            // Insert into Playlistssongs (corrected table name)
            String sql = "INSERT INTO Playlistssongs (PlaylistID, SongID) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, playlistID);
            statement.setInt(2, songId);

            statement.executeUpdate();
            System.out.println("Song added to playlist successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding song.");
            e.printStackTrace();
        }
    }

    private ArrayList<String> viewSongsInPlaylist() {
        System.out.print("Enter playlist name: ");
        String playlistName = scanner.nextLine();

        ArrayList<String> songList = new ArrayList<>();
        try {

           String qur ="SELECT playlists.PlaylistName, allsongs.SongID, allsongs.Title, allsongs.Artist, allsongs.Path FROM playlists INNER JOIN playlistssongs ON playlists.PlaylistId = playlistssongs.PlaylistID INNER JOIN allsongs ON playlistssongs.SongID = allsongs.SongID";
          PreparedStatement   statement = connection.prepareStatement(qur);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                if(playlistName.equals(rs.getString("PlaylistName"))){
                int songId = rs.getInt("SongID");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                songList.add(rs.getString("Path"));
            
                System.out.println("Song ID: " + songId +" | Title: " + title +" | Artist: " + artist);
            }
        }
            return songList;
        } catch (SQLException e) {
            System.out.println("Error fetching songs.");
            e.printStackTrace();
            return null;
        }
    }
    private void playSongInPlaylist() {
        ArrayList<String> path = new ArrayList<>();
        path = viewSongsInPlaylist();
       for(String i: path){
           DemoMusicAudio.playSound(i);
       }
    
    }
}