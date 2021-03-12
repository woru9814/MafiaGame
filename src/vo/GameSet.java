package vo;

import java.io.Serializable;

public class GameSet implements Serializable{//소켓 통신을 위해 직렬화
   private static final long serialVersionUID = 1L;//직렬화 코드 설정    설정안하면  java가 자동으로 할당하는데 이러면 값변경(get,set)시 에러발생가능성
   private int day;//낮시간
   private int night;//밤시간
   private int mf_count;//마피아 수
   private int pl_count;//경찰 수
   private int dt_count;//의사 수
   private int ct_count;//시민 수
   
   @Override
public String toString() {
	return "!set" + day + "/" + night + "/" + mf_count + "/" + pl_count
			+ "/" + dt_count + "/" + ct_count;
}
public GameSet() {
      this.day = 60*5;
      this.night = 60*1;
      this.mf_count = 1;
      this.pl_count = 1;
      this.dt_count = 0;
      this.ct_count = 1;
   }//GameSet test = new GameSet(5,20,1,1,0,2,);
   public GameSet(int day, int night, int mf_count, int pl_count, int dt_count, int ct_count) {
      this.day = day;
      this.night = night;
      this.mf_count = mf_count;
      this.pl_count = pl_count;
      this.dt_count = dt_count;
      this.ct_count = ct_count;
   }
   public int getDay() {
      return day;
   }
   public void setDay(int day) {
      this.day = day;
   }
   public int getNight() {
      return night;
   }
   public void setNight(int night) {
      this.night = night;
   }
   public int getMf_count() {
      return mf_count;
   }
   public void setMf_count(int mf_count) {
      this.mf_count = mf_count;
   }
   public int getPl_count() {
      return pl_count;
   }
   public void setPl_count(int pl_count) {
      this.pl_count = pl_count;
   }
   public int getDt_count() {
      return dt_count;
   }
   public void setDt_count(int dt_count) {
      this.dt_count = dt_count;
   }
   public int getCt_count() {
      return ct_count;
   }
   public void setCt_count(int ct_count) {
      this.ct_count = ct_count;
   }
}