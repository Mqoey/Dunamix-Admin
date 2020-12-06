/*
 * Copyright (c) 2019 ZimRealSoft
 * All Rights Reserved
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution and de-compilation.
 *
 */

package afm.drc.dunamixadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import afm.drc.dunamixadmin.R;

public class Users extends Fragment {


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.users, container, false);

        return root;
    }
}

