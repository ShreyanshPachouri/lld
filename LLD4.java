import java.util.*;

enum PlayStrategyType{
    SEQUENTIAL,
    RANDOM,
    CUSTOM_QUEUE
}

enum DeviceType{
    BLUETOOTH,
    WIRED,
    HEADPHONES,
}

class Song{
    String title;
    String artist;
    String filePath;

    public Song(String title, String artist, String filePath){
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    public String getfilePath(){
        return filePath;
    }
}

class Playlist{
    String playlistName;
    ArrayList<Song> songList;

    public Playlist(String playlistName, ArrayList<Song> songList){
        this.playlistName = playlistName;
        this.songList = songList;
    }

    public String getPlaylistName(){
        return playlistName;
    }

    public ArrayList<Song> getSongs(){
        return songList;
    }

    public int getSize(){
        return songList.size();
    }

    public void addSongToPlaylist(Song s){
        songList.add(s);
    }
}

class HeadphonesAPI{
    public void playSoundViaJack(String song){
        System.out.println("Playing " + song + " via " + DeviceType.HEADPHONES);
    }
}

class BluetoothSpeakerAPI{
    public void playSoundViaBluetooth(String song){
        System.out.println("Playing " + song + " via " + DeviceType.BLUETOOTH);
    }
}

class WiredSpeakerAPI{
    public void playSoundViaCable(String song){
        System.out.println("Playing " + song + " via " + DeviceType.WIRED);
    }
}

interface IAudioOutputDevice{
    public void playAudio(Song s);
}

class HeadphonesAdapter implements IAudioOutputDevice{
    HeadphonesAPI headphones = new HeadphonesAPI();

    public void playAudio(Song s){
        headphones.playSoundViaJack(s.title);
    }
}

class BlutoothSpeakerAdapter implements IAudioOutputDevice{
    BluetoothSpeakerAPI bluetooth = new BluetoothSpeakerAPI();

    public void playAudio(Song s){
        bluetooth.playSoundViaBluetooth(s.title);
    }
}

class WiredSpeakerAdapter implements IAudioOutputDevice{
    WiredSpeakerAPI wiredSpeaker = new WiredSpeakerAPI();

    public void playAudio(Song s){
        wiredSpeaker.playSoundViaCable(s.title);
    }
}

class DeviceFactory{
    static IAudioOutputDevice createDevice(DeviceType device){
        if(device == DeviceType.BLUETOOTH){
            return new BlutoothSpeakerAdapter();
        }

        else if(device == DeviceType.HEADPHONES){
            return new HeadphonesAdapter();
        }

        else{
            return new WiredSpeakerAdapter();
        }
    }
}

abstract class PlayStrategy{
    abstract public void setPlaylist(Playlist p);
    abstract public Song next();
    abstract public boolean hasNext();
    abstract public Song previous();
    abstract public boolean hasPrevious();
    public void addToNext(Song s){};
}

class SequentialPlayStrategy extends PlayStrategy{
    Playlist currentPlaylist;
    int currentIndex;

    public void setPlaylist(Playlist p){
        this.currentPlaylist = p;
    }

    public boolean hasNext(){
        return currentIndex < currentPlaylist.getSize() - 1;
    }

    public boolean hasPrevious(){
        return currentIndex > 0;
    }

    public Song next(){
        if(!hasNext()){
            throw new RuntimeException("There is no next song in the playlist");
        }

        currentIndex ++;
        return currentPlaylist.getSongs().get(currentIndex + 1);
    }

    public Song previous(){
        if(!hasPrevious()){
            throw new RuntimeException("There is no previous song in the playlist");
        }
        
        currentIndex --;
        return currentPlaylist.getSongs().get(currentIndex - 1);
    }
}

