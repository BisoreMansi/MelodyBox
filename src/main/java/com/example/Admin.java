package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Admin {
    int adminID;
   
    private static Connection connection = null;
    private static Scanner scanner = new Scanner(System.in);

    public Admin(int adminID) {
       try {
            connection = ConnectionClassForDM.getConnection(); // Establish connection to the database
            if (connection == null) {
                throw new SQLException("Failed to establish connection.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing database connection: " + e.getMessage());
            e.printStackTrace();
        }
        this.adminID = adminID;
    }

    public boolean loginAdmin() {

        System.out.print("Enter admin Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if(connection == null)
        {
            System.out.println("Database Connection is not Available");
            return false;
        }

        try {
            String sql = "SELECT * FROM Admins WHERE AdminID = ? AND Password = ? AND isAdmin = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, this.adminID);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Admin login successful!");
                return true;
            } else {
                System.out.println("Invalid admin credentials.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error logging in admin.");
            e.printStackTrace();
            return false;
        }
    }

    public void manageSongs() {

        boolean running = true;
        while (running) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Add Song");
            System.out.println("2. Delete Song");
           
            System.out.println("3. View All Songs");

            System.out.println("4. Create Album");
            System.out.println("5. View Albums");
            System.out.println("6. Add Song To Album");
            System.out.println("7. View Album Songs");
            System.out.println("8. Play Album Songs");
            System.out.println("9. Back");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:     //Add Song
                    addSong();
                    break;
                case 2:     // Delete Song
                    deleteSong();
                    break;
               
                case 3:     // View All Songs
                    viewAllSongs();
                    break;
                case 4:
                    createAlbum();
                    break;
                case 5:
                    viewYourAlbums();
                    break;
                case 6:     //Add Song To Album
                    addSongToAlbum();
                    break;
                case 7:     //  View Album Songs
                    viewYourAlbumSongs();
                    break;
                case 8:
                    playYourAlbumSong();
                case 9:     // Back
                    running = false;
                    System.out.println("Exiting Admin Menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }



    private void addSong() {
        System.out.print("Enter song title: ");
        String title = scanner.nextLine();
        System.out.print("Enter artist: ");
        String artist = scanner.nextLine();
        System.out.print("Enter file path: ");
        String path = scanner.nextLine();

        try {
            Song.AddSongToDB(title, artist, path); // Call static method from Song class
        } catch (Exception e) {
            System.out.println("Error adding song.");
            e.printStackTrace();
        }
    }

    private void deleteSong() {
        System.out.print("Enter song title to delete: ");
        String title = scanner.nextLine();

        try {
            String sql = "DELETE FROM AllSongs WHERE Title = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Song deleted successfully.");
            } else {
                System.out.println("No song found with the specified title.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting song.");
            e.printStackTrace();
        }
    }

    

    private void viewAllSongs() {

        try {
            Song.getAllSongs(); // Call static method from Song class
        } catch (Exception e) {
            System.out.println("Error retrieving songs.");
            e.printStackTrace();
        }
    }

    // create Album:
    private void createAlbum(){
        System.out.print("Enter Album Name: ");
        String albumName = scanner.nextLine();

        try {
            // Corrected table and column names based on the database schema
            String sql = "Insert into Album (AlbumName, AdminID) values (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, albumName);
            statement.setInt(2, adminID); // Use the logged-in user's ID
            statement.executeUpdate();
            System.out.println("Album created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating Album.");
            e.printStackTrace();
        }
    }

    public void viewYourAlbums(){
        try {
            
            String sql = "select AlbumID, AlbumName from Album where AdminID =?";
            PreparedStatement stat = connection.prepareStatement(sql);
            System.out.println("admin ID: "+ this.adminID);
            stat.setInt(1, this.adminID); // Use the logged-in user's ID
            ResultSet resultSet = stat.executeQuery();

            while (resultSet.next()) {
                int albumID = resultSet.getInt("AlbumID");
                String name = resultSet.getString("AlbumName");
                System.out.println("Album ID: " + albumID + " | Name: " + name);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Albums.");
            e.printStackTrace();
        }
    }

    private void addSongToAlbum(){      // add song to exsisting album:
        
        System.out.print("Enter Album Name: ");
            String albumName = scanner.nextLine();
        System.out.print("Enter song name: ");
            String title = scanner.nextLine();

 try {
     // get AlbumID from Album table:
     
         String qur = "select AlbumID from Album where AlbumName ='"+albumName+"'";
         int albumID=0;
         PreparedStatement statement = connection.prepareStatement(qur);
         ResultSet rs = statement.executeQuery();
         while(rs.next()){
              albumID = rs.getInt("AlbumID");
         }

     // get songID from song table :
         int songID =0;

         String qur1 = "select SongID from allsongs where Title= '"+title+"'";
         statement = connection.prepareStatement(qur1);
         rs = statement.executeQuery();
         while(rs.next()){
              songID = rs.getInt("SongID");
         }

     String sql = "INSERT INTO AlbumSongs (AlbumID, SongID) VALUES (?, ?)";
      statement = connection.prepareStatement(sql);
      statement.setInt(1, albumID);
      statement.setInt(2, songID);

     statement.executeUpdate();
     System.out.println("Song added to Album successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding song.");
            e.printStackTrace();
        }
    }


    public ArrayList<String> viewYourAlbumSongs(){
        System.out.print("Enter Album name: ");
        String albumName = scanner.nextLine();
        try {
            ArrayList<String> songList = new ArrayList<>();
 String qur ="SELECT Album.AlbumName, allsongs.SongID, allsongs.Title, allsongs.Artist, allsongs.Path, Album.AlbumID FROM Album INNER JOIN AlbumSongs ON Album.AlbumID =AlbumSongs.AlbumID INNER JOIN allsongs ON AlbumSongs.SongID = allsongs.SongID";
            PreparedStatement statement = connection.prepareStatement(qur);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                if(albumName.equals(rs.getString("AlbumName"))){
                int songId = rs.getInt("SongID");
                String title = rs.getString("Title");
                String artist = rs.getString("Artist");
                String path = rs.getString("Path");
                songList.add(path);
            
                System.out.println("Song ID: " + songId +" | Title: " + title +" | Artist: " + artist+" | Path: "+path);
            }
        }
        return songList;
        } catch (SQLException e) {
            System.out.println("Error fetching songs.");
            e.printStackTrace();
            return null;
        }
    }


    public void playYourAlbumSong(){
        ArrayList<String> path = new ArrayList<>();
        path = viewYourAlbumSongs();
       for(String i: path){
           DemoMusicAudio.playSound(i);
       }
   }
    }