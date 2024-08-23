package com.example.downloadmultiplefilesapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface FileDownloadService {
    @GET
    @Headers({
            "x-rapidapi-key: 9514810a91mshaa1e82f038a194dp192b4djsn80ef1486dc10",
            "x-rapidapi-host: spotify-downloader9.p.rapidapi.com"
    })
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
