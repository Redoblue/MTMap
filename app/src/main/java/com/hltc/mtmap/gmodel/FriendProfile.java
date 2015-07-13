package com.hltc.mtmap.gmodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by redoblue on 15-7-12.
 */
public class FriendProfile implements Parcelable {

    public static final Parcelable.Creator<FriendProfile> CREATOR = new Parcelable.Creator<FriendProfile>() {
        public FriendProfile createFromParcel(Parcel source) {
            return new FriendProfile(source);
        }

        public FriendProfile[] newArray(int size) {
            return new FriendProfile[size];
        }
    };
    public User user;
    public GrainStatistics grainStatistics;

    public FriendProfile() {
    }

    protected FriendProfile(Parcel in) {
        this.user = in.readParcelable(User.class.getClassLoader());
        this.grainStatistics = in.readParcelable(GrainStatistics.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.grainStatistics, flags);
    }

    public static class User implements Parcelable {
        public static final Creator<User> CREATOR = new Creator<User>() {
            public User createFromParcel(Parcel source) {
                return new User(source);
            }

            public User[] newArray(int size) {
                return new User[size];
            }
        };
        public long userId;
        public String portrait;
        public String nickName;
        public String remark;
        public String signature;
        public String coverImg;

        public User() {
        }

        protected User(Parcel in) {
            this.userId = in.readLong();
            this.portrait = in.readString();
            this.nickName = in.readString();
            this.remark = in.readString();
            this.signature = in.readString();
            this.coverImg = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.userId);
            dest.writeString(this.portrait);
            dest.writeString(this.nickName);
            dest.writeString(this.remark);
            dest.writeString(this.signature);
            dest.writeString(this.coverImg);
        }
    }

    public static class GrainStatistics implements Parcelable {
        public static final Creator<GrainStatistics> CREATOR = new Creator<GrainStatistics>() {
            public GrainStatistics createFromParcel(Parcel source) {
                return new GrainStatistics(source);
            }

            public GrainStatistics[] newArray(int size) {
                return new GrainStatistics[size];
            }
        };
        public int chihe;
        public int wanle;
        public int other;

        public GrainStatistics() {
        }

        protected GrainStatistics(Parcel in) {
            this.chihe = in.readInt();
            this.wanle = in.readInt();
            this.other = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.chihe);
            dest.writeInt(this.wanle);
            dest.writeInt(this.other);
        }
    }
}
