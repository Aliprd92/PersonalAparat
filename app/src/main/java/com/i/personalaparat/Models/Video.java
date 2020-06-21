package com.i.personalaparat.Models;

import java.util.List;

public class Video {

    private Ui ui;
    private List<Videobyuser> videobyuser;

    public Ui getUi() {
        return ui;
    }

    public void setUi(Ui ui) {
        this.ui = ui;
    }

    public List<Videobyuser> getVideobyuser() {
        return videobyuser;
    }

    public void setVideobyuser(List<Videobyuser> videobyuser) {
        this.videobyuser = videobyuser;
    }

    public static class Ui {

        private String pagingForward;
        private Object pagingBack;

        public String getPagingForward() {
            return pagingForward;
        }

        public void setPagingForward(String pagingForward) {
            this.pagingForward = pagingForward;
        }

        public Object getPagingBack() {
            return pagingBack;
        }

        public void setPagingBack(Object pagingBack) {
            this.pagingBack = pagingBack;
        }
    }

    public static class Videobyuser {

        private String id;
        private String title;
        private int visit_cnt;
        private String uid;
        private String big_poster;
        private int duration;
        private String create_date;
        private int sdate_timediff;
        private String frame;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getVisit_cnt() {
            return visit_cnt;
        }

        public void setVisit_cnt(int visit_cnt) {
            this.visit_cnt = visit_cnt;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getBig_poster() {
            return big_poster;
        }

        public void setBig_poster(String big_poster) {
            this.big_poster = big_poster;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public int getSdate_timediff() {
            return sdate_timediff;
        }

        public void setSdate_timediff(int sdate_timediff) {
            this.sdate_timediff = sdate_timediff;
        }

        public String getFrame() {
            return frame;
        }

        public void setFrame(String frame) {
            this.frame = frame;
        }
    }
}
