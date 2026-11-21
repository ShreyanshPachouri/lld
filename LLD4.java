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

abstract class IAudioOutputDevice{
    public abstract void playAudio(Song s);
}

class HeadphonesAdapter extends IAudioOutputDevice{
    HeadphonesAPI headphones = new HeadphonesAPI();

    public void playAudio(Song s){
        headphones.playSoundViaJack(s.title);
    }
}

class BlutoothSpeakerAdapter extends IAudioOutputDevice{
    BluetoothSpeakerAPI bluetooth = new BluetoothSpeakerAPI();

    public void playAudio(Song s){
        bluetooth.playSoundViaBluetooth(s.title);
    }
}

class WiredSpeakerAdapter extends IAudioOutputDevice{
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

        else if (device == DeviceType.WIRED){
            return new WiredSpeakerAdapter();
        }

        throw new RuntimeException("Invalid device type");
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
    int currentIndex = -1;

    public void setPlaylist(Playlist p){
        this.currentPlaylist = p;
        this.currentIndex = -1;
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
        return currentPlaylist.getSongs().get(currentIndex);
    }

    public Song previous(){
        if(!hasPrevious()){
            throw new RuntimeException("There is no previous song in the playlist");
        }
        
        currentIndex --;
        return currentPlaylist.getSongs().get(currentIndex);
    }
}

class RandomPlayStrategy extends PlayStrategy{
    Playlist currentPlaylist;
    ArrayList<Song> remainingSongs = new ArrayList<>();
    Stack<Song> history = new Stack<>();
    Random random = new Random();

    public void setPlaylist(Playlist p){
        this.currentPlaylist = p;
        this.remainingSongs = new ArrayList<>(p.getSongs());
        this.history = new Stack<>();
    }

    public boolean hasNext(){
        return !remainingSongs.isEmpty();
    }

    public boolean hasPrevious(){
        return history.size() > 1;
    }

    public Song next(){
        if(!hasNext()){
            throw new RuntimeException("There is no next song in the playlist");
        }

        int idx = random.nextInt(remainingSongs.size());
        Song selected = remainingSongs.get(idx);

        int last = remainingSongs.size() - 1;
        remainingSongs.set(idx, remainingSongs.get(last));
        remainingSongs.remove(last);

        history.push(selected);
        return selected;
    }

    public Song previous(){
        if(!hasPrevious()){
            throw new RuntimeException("There is no previous song in the playlist");
        }

        history.pop();
        return history.peek();
    }
}

class CustomQueueStrategy extends PlayStrategy{
    Playlist currentPlaylist;
    int currentIndex = -1;
    Queue<Song> nextQueue = new LinkedList<>();
    Stack<Song> prevStack = new Stack<>();

    public void setPlaylist(Playlist p){
        this.currentPlaylist = p;
        this.currentIndex = -1;
        this.nextQueue.clear();
        this.prevStack.clear();
    }

    public boolean hasNext(){
        return !nextQueue.isEmpty() || currentIndex < currentPlaylist.getSize() - 1;
    }

    public boolean hasPrevious(){
        return !prevStack.isEmpty() || currentIndex > 0;
    }

    public Song next(){
        if(!hasNext()){
            throw new RuntimeException("There is no next song in the playlist");
        }

        if(!nextQueue.isEmpty()){
            Song s = nextQueue.poll();
            prevStack.push(s);
            int pos = currentPlaylist.getSongs().indexOf(s);
            if(pos != -1){
                currentIndex = pos;
            }
            return s;
        }

        currentIndex ++;
        return currentPlaylist.getSongs().get(currentIndex);
    }

    public Song previous(){
        if(!hasPrevious()){
            throw new RuntimeException("There is no previous song in the playlist");
        }

        if(!prevStack.isEmpty()){
            Song s = prevStack.pop();
            int pos = currentPlaylist.getSongs().indexOf(s);
            if(pos != -1){
                currentIndex = pos;
            }
            return s;
        }

        currentIndex --;
        return currentPlaylist.getSongs().get(currentIndex);
    }

    public void addToNext(Song s){
        nextQueue.add(s);
    }
}

class PlaylistManager{
    private static PlaylistManager instance;
    private HashMap<String, Playlist> map;

    private PlaylistManager(){
        this.map = new HashMap<>();
    }

    public static synchronized PlaylistManager getInstance(){
        if(instance == null){
            instance = new PlaylistManager();
        }

        return instance;
    }

    public void createPlaylist(String name){
        if(map.containsKey(name)){
            throw new RuntimeException("Name is not unique");
        }

        map.put(name, new Playlist(name, new ArrayList<Song>()));
    }

    public void addSongToPlaylist(String playlistName, Song s){
        if(!map.containsKey(playlistName)){
            throw new RuntimeException("Playlist does not exist");
        }

        map.get(playlistName).addSongToPlaylist(s);
    }

    public Playlist getPlaylist(String name){
        if(!map.containsKey(name)){
            throw new RuntimeException("Playlist does not exist");
        }

        return map.get(name);
    }
}

class DeviceManager{
    private static DeviceManager instance;
    private IAudioOutputDevice currentOutputDevice;

    private DeviceManager(){}

    public static DeviceManager getInstance(){
        if(instance == null){
            instance = new DeviceManager();
        }

        return instance;
    }

    public void connect(DeviceType deviceType){
       currentOutputDevice = DeviceFactory.createDevice(deviceType);
    }

    public IAudioOutputDevice getOutputDevice(){
        return this.currentOutputDevice;
    }

    public boolean hasOutputDevice(){
        return currentOutputDevice != null;
    }
}

class StrategyManager{
    private static StrategyManager instance;

    private StrategyManager(){};

    public static StrategyManager getInstance(){
        if(instance == null){
            instance = new StrategyManager();
        }

        return instance;
    }

    public PlayStrategy getStrategy(PlayStrategyType type){
        if(type == PlayStrategyType.SEQUENTIAL){
            return new SequentialPlayStrategy();
        }

        else if(type == PlayStrategyType.RANDOM){
            return new RandomPlayStrategy();
        }

        else if(type == PlayStrategyType.CUSTOM_QUEUE){
            return new CustomQueueStrategy();
        }

        throw new RuntimeException("Invalid strategy type");
    }
}

class AudioEngine{
    Song currentSong;
    boolean songIsPaused = false;

    public String getCurrentSongTitle(){
        if(currentSong == null){
            throw new RuntimeException("No song is playing currently");
        }

        return this.currentSong.title;
    }

    public boolean isPaused(){
        return this.songIsPaused;
    }

    public void play(IAudioOutputDevice device, Song song){
        this.currentSong = song;
        this.songIsPaused = false;
        device.playAudio(song);
    }

    public void pause(){
        if(isPaused()){
            throw new RuntimeException("Cannot pause a song which is already paused");
        }

        this.songIsPaused = true;
    }
}

class MusicPlayerFacade{
    private static MusicPlayerFacade instance;
    AudioEngine audioEngine = new AudioEngine();
    Playlist loadedPlaylist;
    PlayStrategy playStrategy;
    
    DeviceManager deviceManager = DeviceManager.getInstance();
    StrategyManager strategyManager = StrategyManager.getInstance();
    PlaylistManager playlistManager = PlaylistManager.getInstance();

    private MusicPlayerFacade(){}

    public static synchronized MusicPlayerFacade getInstance(){
        if(instance == null){
            instance = new MusicPlayerFacade();
        }

        return instance;
    }

    public void connectDevice(DeviceType device){
        deviceManager.connect(device);
    }

    public void setPlayStrategy(PlayStrategyType strategy){
        playStrategy = strategyManager.getStrategy(strategy);
    }

    public void loadPlaylist(String name){
        loadedPlaylist = playlistManager.getPlaylist(name);
        playStrategy.setPlaylist(loadedPlaylist);
    }

    public void playSong(Song song){
        audioEngine.play(deviceManager.getOutputDevice(), song);
    }

    public void pauseSong(Song song){
        audioEngine.pause();
    }

    public void playAllTracks(){
        while(playStrategy.hasNext()){
            playNextTrack();
        }
    }

    public void playNextTrack(){
        playSong(playStrategy.next());
    }

    public void playPreviousTrack(){
        playSong(playStrategy.previous());
    }

    public void enqueueNext(Song song){
        playStrategy.addToNext(song);
    }
}

class MusicPlayerApplication{
    private static MusicPlayerApplication instance;
    private ArrayList<Song> songList;

    PlaylistManager playlistManager = PlaylistManager.getInstance();
    MusicPlayerFacade musicPlayerFacade = MusicPlayerFacade.getInstance();

    private MusicPlayerApplication(){
        songList = new ArrayList<>();
    }

    public static synchronized MusicPlayerApplication getInstance(){
        if(instance == null){
            instance = new MusicPlayerApplication();
        }

        return instance;
    }

    public void createSongInLibrary(String title, String artist, String path){
        songList.add(new Song(title, artist, path));
    }

    public Song findSongByTitle(String title){
        for(Song song: songList){
            if(song.title.equals(title)){
                return song;
            }
        }

        throw new RuntimeException("Song not found");
    }

    public void createPlaylist(String name){
        playlistManager.createPlaylist(name);
    }

    public void addSongToPlaylist(String playlistName, String title){
        for(Song song: songList){
            if(song.title.equals(title)){
                playlistManager.addSongToPlaylist(playlistName, song);
                return;
            }
        }

        throw new RuntimeException("Song not found");
    }

    public void connectAudioDevice(DeviceType device){
        musicPlayerFacade.connectDevice(device);
    }

    public void selectPlayStrategy(PlayStrategyType strategy){
        musicPlayerFacade.setPlayStrategy(strategy);
    }

    public void loadPlaylist(String name){
        musicPlayerFacade.loadPlaylist(name);
    }

    public void playSingleSong(String title){
        for(Song song: songList){
            if(song.title.equals(title)){
                musicPlayerFacade.playSong(song);
                break;
            }
        }
        
        throw new RuntimeException("Song not found");
    }

    public void pauseCurrentSong(String title){
        for(Song song: songList){
            if(song.title.equals(title)){
                musicPlayerFacade.pauseSong(song);
                break;
            }
        }
        
        throw new RuntimeException("Song not found");
    }

    public void playAllTracksInPlaylist(){
        musicPlayerFacade.playAllTracks();
    }

    public void playPreviousTrackInPlaylist(){
        musicPlayerFacade.playPreviousTrack();
    }

    public void queueSongNext(String title){
         for(Song song: songList){
            if(song.title.equals(title)){
                musicPlayerFacade.enqueueNext(song);
                break;
            }
        }
        
        throw new RuntimeException("Song not found");
    }
}

