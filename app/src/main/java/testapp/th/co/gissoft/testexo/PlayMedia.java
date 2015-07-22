package testapp.th.co.gissoft.testexo;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer.util.Util;

/**
 * Created by gissoft82 on 7/21/2015.
 */
public class PlayMedia {
    private String url = "http://203.146.129.246:2080";
    private Boolean flagStop = Boolean.FALSE;
    private DemoPlayer player;
    private Context mContext;
    public PlayMediaListener mListener;
    private static PlayMedia instance;
    public static PlayMedia getInstance(Context mContext){
        if(instance == null)
            instance = new PlayMedia(mContext);
        return instance;
    }

    public static boolean isCreated(){
        return instance!=null;
    }

    public Boolean returnIsPlating(){
        if(player == null)
            return false;
        return player.getPlayWhenReady();
    }

    public PlayMedia(Context context){
        this.mContext = context;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void addListener(PlayMediaListener listener){
        this.mListener = listener;
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void preparePlayer() {
        if(player == null){
            mListener.onLoading();
            String userAgent = Util.getUserAgent(mContext, "ExoPlayerDemo");
            Uri uri = Uri.parse(url);
            player = new DemoPlayer(new ExtractorRendererBuilder(mContext,userAgent, uri, null, new Mp3Extractor()));
            player.prepare();
            player.setPlayWhenReady(true);
        }
    }

    public void playStart(){
        try {
            preparePlayer();
            flagStop = Boolean.FALSE;
            //TODO PLAY
            mListener.onPlayed();
/*            bt_play.setVisibility(View.VISIBLE);
            bt_play_load.setVisibility(View.GONE);*/
        }catch (Exception ex){

        }
    }

    public void pausePlay(){
        mListener.onPause();
    }

    public Boolean getFlagStop() {
        return flagStop;
    }

    public void force_pause(){
        releasePlayer();
    }

    public void playMedia(boolean check) {
        try {
            if (check) {
                preparePlayer();

                flagStop = Boolean.FALSE;
            } else {

                releasePlayer();
            }
        } catch (Exception e) {
        }

        //TODO  PLAY
        mListener.onPlayed();
    }

    public interface PlayMediaListener{
        public void onPlayed();
        public void onLoading();
        public void onPause();

    }
}
