/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.view.NotificationHeaderView;
import android.view.View;

import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;

/**
 * Wraps the actual notification content view; used to implement behaviors which are different for
 * the individual templates and custom views.
 */
public abstract class NotificationViewWrapper implements TransformableView {

    protected final View mView;
    protected boolean mDark;

    public static NotificationViewWrapper wrap(Context ctx, View v) {
        if (v.getId() == com.android.internal.R.id.status_bar_latest_event_content) {
            if ("bigPicture".equals(v.getTag())) {
                return new NotificationBigPictureTemplateViewWrapper(ctx, v);
            } else if ("bigText".equals(v.getTag())) {
                return new NotificationBigTextTemplateViewWrapper(ctx, v);
            }
            return new NotificationTemplateViewWrapper(ctx, v);
        } else if (v instanceof NotificationHeaderView) {
            return new NotificationHeaderViewWrapper(ctx, v);
        } else {
            return new NotificationCustomViewWrapper(v);
        }
    }

    protected NotificationViewWrapper(View view) {
        mView = view;
    }

    /**
     * In dark mode, we draw as little as possible, assuming a black background.
     *
     * @param dark whether we should display ourselves in dark mode
     * @param fade whether to animate the transition if the mode changes
     * @param delay if fading, the delay of the animation
     */
    public void setDark(boolean dark, boolean fade, long delay) {
        mDark = dark;
    }

    /**
     * Notifies this wrapper that the content of the view might have changed.
     * @param notification
     */
    public void notifyContentUpdated(StatusBarNotification notification) {
        mDark = false;
    };

    /**
     * Update the appearance of the expand button.
     *
     * @param expandable should this view be expandable
     * @param onClickListener the listener to invoke when the expand affordance is clicked on
     */
    public void updateExpandability(boolean expandable, View.OnClickListener onClickListener) {}

    /**
     * @return the notification header if it exists
     */
    public NotificationHeaderView getNotificationHeader() {
        return null;
    }

    @Override
    public TransformState getCurrentState(int fadingView) {
        return null;
    }

    @Override
    public void transformTo(TransformableView notification, Runnable endRunnable) {
        // By default we are fading out completely
        CrossFadeHelper.fadeOut(mView, endRunnable);
    }

    @Override
    public void transformTo(TransformableView notification, float transformationAmount) {
        CrossFadeHelper.fadeOut(mView, transformationAmount);
    }

    @Override
    public void transformFrom(TransformableView notification) {
        // By default we are fading in completely
        CrossFadeHelper.fadeIn(mView);
    }

    @Override
    public void transformFrom(TransformableView notification, float transformationAmount) {
        CrossFadeHelper.fadeIn(mView, transformationAmount);
    }

    @Override
    public void setVisible(boolean visible) {
        mView.animate().cancel();
        mView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
