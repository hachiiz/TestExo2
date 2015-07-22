package testapp.th.co.gissoft.testexo;

/**
 * Created by gissoft82 on 7/22/2015.
 */
import android.widget.TextView;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.Format;

/**
 * A {@link com.google.android.exoplayer.TrackRenderer} that periodically updates debugging information displayed by a
 * {@link TextView}.
 */
/* package */ class DebugTrackRenderer extends TrackRenderer implements Runnable {

    private final TextView textView;
    private final DemoPlayer player;
    private final MediaCodecTrackRenderer renderer;

    private volatile boolean pendingFailure;
    private volatile long currentPositionUs;

    public DebugTrackRenderer(TextView textView, DemoPlayer player,
                              MediaCodecTrackRenderer renderer) {
        this.textView = textView;
        this.player = player;
        this.renderer = renderer;
    }

    public void injectFailure() {
        pendingFailure = true;
    }

    @Override
    protected boolean isEnded() {
        return true;
    }

    @Override
    protected boolean isReady() {
        return true;
    }

    @Override
    protected int doPrepare(long positionUs) throws ExoPlaybackException {
        maybeFail();
        return STATE_PREPARED;
    }

    @Override
    protected void doSomeWork(long positionUs, long elapsedRealtimeUs) throws ExoPlaybackException {
        maybeFail();
        if (positionUs < currentPositionUs || positionUs > currentPositionUs + 1000000) {
            currentPositionUs = positionUs;
            textView.post(this);
        }
    }

    @Override
    public void run() {
        textView.setText(getRenderString());
    }

    private String getRenderString() {
        return getQualityString() + " " + renderer.codecCounters.getDebugString();
    }

    private String getQualityString() {
        Format format = player.getVideoFormat();
        return format == null ? "id:? br:? h:?"
                : "id:" + format.id + " br:" + format.bitrate + " h:" + format.height;
    }

   // @Override
    protected long getCurrentPositionUs() {
        return currentPositionUs;
    }

    @Override
    protected long getDurationUs() {
        return TrackRenderer.MATCH_LONGEST_US;
    }

    @Override
    protected long getBufferedPositionUs() {
        return TrackRenderer.END_OF_TRACK_US;
    }

    @Override
    protected void seekTo(long timeUs) {
        currentPositionUs = timeUs;
    }

    private void maybeFail() throws ExoPlaybackException {
        if (pendingFailure) {
            pendingFailure = false;
            throw new ExoPlaybackException("fail() was called on DebugTrackRenderer");
        }
    }

}