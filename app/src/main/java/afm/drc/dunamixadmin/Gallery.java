/*
 * Copyright (c) 2019 ZimRealSoft
 * All Rights Reserved
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution and de-compilation.
 *
 */

package afm.drc.dunamixadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import afm.drc.dunamixadmin.R;

public class Gallery extends Fragment {


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.gallery, container, false);

        Button upload = root.findViewById(R.id.upload_pic);
        Button remove= root.findViewById(R.id.remove);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadPicturesActivity.class);
                startActivity(intent);

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GetAllPicturesActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}