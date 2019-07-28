package com.orpheusdroid.screenrecorder.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.orpheusdroid.screenrecorder.Config;
import com.orpheusdroid.screenrecorder.Const;
import com.orpheusdroid.screenrecorder.R;
import com.orpheusdroid.screenrecorder.adapter.VideoListAdapter;
import com.orpheusdroid.screenrecorder.adapter.models.videolist.Video;
import com.orpheusdroid.screenrecorder.adapter.models.videolist.VideoHeader;
import com.orpheusdroid.screenrecorder.adapter.models.videolist.VideoItem;
import com.orpheusdroid.screenrecorder.adapter.models.videolist.VideoListItem;
import com.orpheusdroid.screenrecorder.interfaces.VideoFragmentListener;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class VideoListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, VideoFragmentListener {

    private VideoListViewModel mViewModel;

    public static VideoListFragment newInstance() {
        return new VideoListFragment();
    }


    private TextView message;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView videoList;
    private ArrayList<VideoListItem> consolidatedList = new ArrayList<>();
    private VideoListAdapter videoListAdapter;

    /**
     * Method to check if the file's meme type is video
     *
     * @param path String - path to the file
     * @return boolean
     */
    private static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_list_fragment, container, false);

        videoList = view.findViewById(R.id.videoList);
        videoList.setHasFixedSize(true);

        message = view.findViewById(R.id.message_tv);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setVideoFragmentListener(this);
            ((MainActivity) getActivity()).init(Const.VIDEO_FRAGMENT_EXTDIR_REQUEST_CODE);
        }

        return view;
    }

    private void listVideos() {
        File directory = new File(Config.getInstance(getActivity()).getSaveLocation());
        //Remove directory pointers and other files from the list
        if (!directory.exists()) {
            directory.mkdirs();
            Log.d(Const.TAG, "Directory missing! Creating dir");
        }

        ArrayList<File> filesList = new ArrayList<File>();
        if (directory.isDirectory() && directory.exists()) {
            Log.d(Const.TAG, "Getting videos for " + directory.getAbsolutePath());
            filesList.addAll(Arrays.asList(getVideos(directory.listFiles())));
        }

        new GetVideosAsync().execute(filesList.toArray(new File[0]));

        Log.d(Const.TAG, "Fetching data");
    }

    /**
     * Filter all video files from array of files
     *
     * @param files File[] containing files from a directory
     * @return File[] containing only video files
     */
    private File[] getVideos(File[] files) {
        List<File> newFiles = new ArrayList<>();
        for (File file : files) {
            if (!file.isDirectory() && isVideoFile(file.getPath()))
                newFiles.add(file);
        }
        return newFiles.toArray(new File[newFiles.size()]);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(getString(R.string.title_videos));

        mViewModel = ViewModelProviders.of(this).get(VideoListViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onRefresh() {
        listVideos();
    }

    @Override
    public void onStorageResult(boolean result) {
        Log.d(Const.TAG, "Loading videos after result" + result);
        if (result)
            listVideos();
    }

    class GetVideosAsync extends AsyncTask<File[], Integer, ArrayList<VideoListItem>> {
        //ProgressDialog progress;
        File[] files;
        ContentResolver resolver;

        GetVideosAsync() {
            resolver = getActivity().getApplicationContext().getContentResolver();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Set refreshing to true
            consolidatedList = new ArrayList<>();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(ArrayList<VideoListItem> videos) {
            //If the directory has no videos, remove recyclerview from rootview and show empty message.
            // Else set recyclerview and remove message textview
            if (videos.isEmpty()) {
                videoList.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
            } else {
                //Sort the videos in a descending order
                videoListAdapter = new VideoListAdapter(videos, getContext());
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                //videoList.setLayoutManager(layoutManager);
                videoList.setLayoutManager(layoutManager);
                videoList.setAdapter(videoListAdapter);

                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return videoListAdapter.getItemViewType(position) == VideoListItem.TYPE_HEADER ? layoutManager.getSpanCount() : 1;
                    }
                });

                Log.d(Const.TAG, videos.toString());

                videoList.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
            }
            //Finish refreshing
            swipeRefreshLayout.setRefreshing(false);
        }

        /**
         * Method to add date sections to videos list
         * <p>
         * <p></p>Check if a new Section is to be added by comparing the difference of the section date
         * and the video's last modified date</p>
         *
         * @param current Date of current video
         * @param next    Date of next video
         * @return boolean if a new section must be added
         */
        private boolean addNewSection(Date current, Date next) {
            Calendar currentSectionDate = toCalendar(current.getTime());
            Calendar nextVideoDate = toCalendar(next.getTime());

            // Get the represented date in milliseconds
            long milis1 = currentSectionDate.getTimeInMillis();
            long milis2 = nextVideoDate.getTimeInMillis();

            // Calculate difference in milliseconds
            int dayDiff = (int) Math.abs((milis2 - milis1) / (24 * 60 * 60 * 1000));
            Log.d(Const.TAG, "Date diff is: " + (dayDiff));
            return dayDiff > 0;
        }

        /**
         * Method to return a Calander object from the timestamp
         *
         * @param timestamp long timestamp
         * @return Calendar
         */
        private Calendar toCalendar(long timestamp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            Log.d(Const.TAG, "Progress is :" + values[0]);
        }

        @Override
        protected ArrayList<VideoListItem> doInBackground(File[]... arg) {
            //Get video file name, Uri and video thumbnail from mediastore
            files = arg[0];
            ArrayList<Video> videosList = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (!file.isDirectory() && isVideoFile(file.getPath())) {
                    Video video = new Video(file.getName(),
                            file,
                            DateFormat.format("dd/MM/yyyy", new Date(file.lastModified())).toString()
                    );
                    getBitmap(file, video);
                    videosList.add(video);
                    //Update progress dialog
                    publishProgress(i);
                }
            }

            HashMap<String, List<Video>> groupedHashMap = groupDataIntoHashMap(videosList);


            for (String date : groupedHashMap.keySet()) {
                VideoHeader dateItem = new VideoHeader();
                dateItem.setDate(date);
                consolidatedList.add(dateItem);


                for (Video video : groupedHashMap.get(date)) {
                    VideoItem generalItem = new VideoItem();
                    generalItem.setVideo(video);
                    consolidatedList.add(generalItem);
                }
            }

            return consolidatedList;
        }

        private HashMap<String, List<Video>> groupDataIntoHashMap(List<Video> listOfPojosOfJsonArray) {

            HashMap<String, List<Video>> groupedHashMap = new HashMap<>();

            for (Video pojoOfJsonArray : listOfPojosOfJsonArray) {

                String hashMapKey = pojoOfJsonArray.getLastModified().toString();

                if (groupedHashMap.containsKey(hashMapKey)) {
                    // The key is already in the HashMap; add the pojo object
                    // against the existing key.
                    groupedHashMap.get(hashMapKey).add(pojoOfJsonArray);
                } else {
                    // The key is not there in the HashMap; create a new key-value pair
                    List<Video> list = new ArrayList<>();
                    list.add(pojoOfJsonArray);
                    groupedHashMap.put(hashMapKey, list);
                }
            }


            return groupedHashMap;
        }

        /**
         * Method to get thumbnail from mediastore for video file
         *
         * @param file File object of the video
         */
        void getBitmap(File file, Video video) {
            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
            Cursor cursor = resolver.query(MediaStore.Video.Media.getContentUri("external"),
                    projection,
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{file.getPath()}, null);

            if (cursor != null && cursor.moveToNext()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int id = cursor.getInt(idColumn);
                Bitmap thumbNail = MediaStore.Video.Thumbnails.getThumbnail(resolver, id,
                        MediaStore.Video.Thumbnails.MINI_KIND, null);
                Log.d(Const.TAG, "Retrieved thumbnail for file: " + file.getName());
                cursor.close();
                video.setThumbnail(thumbNail);
            }

        }
    }
}
