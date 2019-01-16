package com.example.kerne.potato;

/***
 * 采集信息的类
 */

public class Species {

    private String planting_date;
    private double sprout_rate;
    private String leaf_colour;
    private String emergence_date;
    private double emergence_rate;
    private String squaring_stage;
    private String blooming;
    private String corolla_colour;
    private String flowering;
    private String stem_colour;
    private String openpollinated;
    private String maturing_stage;
    private int growing_period;
    private String uniformity_of_tuber_size;
    private String tuber_shape;
    private String skin_smoothness;
    private String eye_depth;
    private String skin_colour;
    private String flesh_colour;
    private Boolean is_choozen;
    private String remark;
    private int harvest_num;
    private int lm_num;
    private double lm_weight;
    private int s_num;
    private double s_weight;
    private double commercial_rate;
    private double plot_yield1;
    private double plot_yield2;
    private double plot_yield3;
    private double acre_yield;
    private String plant_height10;
    private double plant_height_avg;
    private String branch_number10;
    private double branch_number_avg;
    private String yield10;
    private String spare1;
    private String spare2;

    public Species(String planting_date, double sprout_rate, String leaf_colour, String emergence_date, double emergence_rate, String squaring_stage, String blooming, String corolla_colour, String flowering, String stem_colour, String openpollinated, String maturing_stage, int growing_period, String uniformity_of_tuber_size, String tuber_shape, String skin_smoothness, String eye_depth, String skin_colour, String flesh_colour, Boolean is_choozen, String remark, int harvest_num, int lm_num, double lm_weight, int s_num, double s_weight, double commercial_rate, double plot_yield1, double plot_yield2, double plot_yield3, double acre_yield, String plant_height10, double plant_height_avg, String branch_number10, double branch_number_avg, String yield10, String spare1, String spare2) {
        this.planting_date = planting_date;
        this.sprout_rate = sprout_rate;
        this.leaf_colour = leaf_colour;
        this.emergence_date = emergence_date;
        this.emergence_rate = emergence_rate;
        this.squaring_stage = squaring_stage;
        this.blooming = blooming;
        this.corolla_colour = corolla_colour;
        this.flowering = flowering;
        this.stem_colour = stem_colour;
        this.openpollinated = openpollinated;
        this.maturing_stage = maturing_stage;
        this.growing_period = growing_period;
        this.uniformity_of_tuber_size = uniformity_of_tuber_size;
        this.tuber_shape = tuber_shape;
        this.skin_smoothness = skin_smoothness;
        this.eye_depth = eye_depth;
        this.skin_colour = skin_colour;
        this.flesh_colour = flesh_colour;
        this.is_choozen = is_choozen;
        this.remark = remark;
        this.harvest_num = harvest_num;
        this.lm_num = lm_num;
        this.lm_weight = lm_weight;
        this.s_num = s_num;
        this.s_weight = s_weight;
        this.commercial_rate = commercial_rate;
        this.plot_yield1 = plot_yield1;
        this.plot_yield2 = plot_yield2;
        this.plot_yield3 = plot_yield3;
        this.acre_yield = acre_yield;
        this.plant_height10 = plant_height10;
        this.plant_height_avg = plant_height_avg;
        this.branch_number10 = branch_number10;
        this.branch_number_avg = branch_number_avg;
        this.yield10 = yield10;
        this.spare1 = spare1;
        this.spare2 = spare2;
    }

    public String getPlanting_date() {
        return planting_date;
    }

    public void setPlanting_date(String planting_date) {
        this.planting_date = planting_date;
    }

    public double getSprout_rate() {
        return sprout_rate;
    }

    public void setSprout_rate(double sprout_rate) {
        this.sprout_rate = sprout_rate;
    }

    public String getLeaf_colour() {
        return leaf_colour;
    }

    public void setLeaf_colour(String leaf_colour) {
        this.leaf_colour = leaf_colour;
    }

    public String getEmergence_date() {
        return emergence_date;
    }

    public void setEmergence_date(String emergence_date) {
        this.emergence_date = emergence_date;
    }

    public double getEmergence_rate() {
        return emergence_rate;
    }

    public void setEmergence_rate(double emergence_rate) {
        this.emergence_rate = emergence_rate;
    }

    public String getSquaring_stage() {
        return squaring_stage;
    }

    public void setSquaring_stage(String squaring_stage) {
        this.squaring_stage = squaring_stage;
    }

    public String getBlooming() {
        return blooming;
    }

    public void setBlooming(String blooming) {
        this.blooming = blooming;
    }

    public String getCorolla_colour() {
        return corolla_colour;
    }

    public void setCorolla_colour(String corolla_colour) {
        this.corolla_colour = corolla_colour;
    }

    public String getFlowering() {
        return flowering;
    }

    public void setFlowering(String flowering) {
        this.flowering = flowering;
    }

    public String getStem_colour() {
        return stem_colour;
    }

    public void setStem_colour(String stem_colour) {
        this.stem_colour = stem_colour;
    }

    public String getOpenpollinated() {
        return openpollinated;
    }

    public void setOpenpollinated(String openpollinated) {
        this.openpollinated = openpollinated;
    }

    public String getMaturing_stage() {
        return maturing_stage;
    }

    public void setMaturing_stage(String maturing_stage) {
        this.maturing_stage = maturing_stage;
    }

    public int getGrowing_period() {
        return growing_period;
    }

    public void setGrowing_period(int growing_period) {
        this.growing_period = growing_period;
    }

    public String getUniformity_of_tuber_size() {
        return uniformity_of_tuber_size;
    }

    public void setUniformity_of_tuber_size(String uniformity_of_tuber_size) {
        this.uniformity_of_tuber_size = uniformity_of_tuber_size;
    }

    public String getTuber_shape() {
        return tuber_shape;
    }

    public void setTuber_shape(String tuber_shape) {
        this.tuber_shape = tuber_shape;
    }

    public String getSkin_smoothness() {
        return skin_smoothness;
    }

    public void setSkin_smoothness(String skin_smoothness) {
        this.skin_smoothness = skin_smoothness;
    }

    public String getEye_depth() {
        return eye_depth;
    }

    public void setEye_depth(String eye_depth) {
        this.eye_depth = eye_depth;
    }

    public String getSkin_colour() {
        return skin_colour;
    }

    public void setSkin_colour(String skin_colour) {
        this.skin_colour = skin_colour;
    }

    public String getFlesh_colour() {
        return flesh_colour;
    }

    public void setFlesh_colour(String flesh_colour) {
        this.flesh_colour = flesh_colour;
    }

    public Boolean getIs_choozen() {
        return is_choozen;
    }

    public void setIs_choozen(Boolean is_choozen) {
        this.is_choozen = is_choozen;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getHarvest_num() {
        return harvest_num;
    }

    public void setHarvest_num(int harvest_num) {
        this.harvest_num = harvest_num;
    }

    public int getLm_num() {
        return lm_num;
    }

    public void setLm_num(int lm_num) {
        this.lm_num = lm_num;
    }

    public double getLm_weight() {
        return lm_weight;
    }

    public void setLm_weight(double lm_weight) {
        this.lm_weight = lm_weight;
    }

    public int getS_num() {
        return s_num;
    }

    public void setS_num(int s_num) {
        this.s_num = s_num;
    }

    public double getS_weight() {
        return s_weight;
    }

    public void setS_weight(double s_weight) {
        this.s_weight = s_weight;
    }

    public double getCommercial_rate() {
        return commercial_rate;
    }

    public void setCommercial_rate(double commercial_rate) {
        this.commercial_rate = commercial_rate;
    }

    public double getPlot_yield1() {
        return plot_yield1;
    }

    public void setPlot_yield1(double plot_yield1) {
        this.plot_yield1 = plot_yield1;
    }

    public double getPlot_yield2() {
        return plot_yield2;
    }

    public void setPlot_yield2(double plot_yield2) {
        this.plot_yield2 = plot_yield2;
    }

    public double getPlot_yield3() {
        return plot_yield3;
    }

    public void setPlot_yield3(double plot_yield3) {
        this.plot_yield3 = plot_yield3;
    }

    public double getAcre_yield() {
        return acre_yield;
    }

    public void setAcre_yield(double acre_yield) {
        this.acre_yield = acre_yield;
    }

    public String getPlant_height10() {
        return plant_height10;
    }

    public void setPlant_height10(String plant_height10) {
        this.plant_height10 = plant_height10;
    }

    public double getPlant_height_avg() {
        return plant_height_avg;
    }

    public void setPlant_height_avg(double plant_height_avg) {
        this.plant_height_avg = plant_height_avg;
    }

    public String getBranch_number10() {
        return branch_number10;
    }

    public void setBranch_number10(String branch_number10) {
        this.branch_number10 = branch_number10;
    }

    public double getBranch_number_avg() {
        return branch_number_avg;
    }

    public void setBranch_number_avg(double branch_number_avg) {
        this.branch_number_avg = branch_number_avg;
    }

    public String getYield10() {
        return yield10;
    }

    public void setYield10(String yield10) {
        this.yield10 = yield10;
    }

    public String getSpare1() {
        return spare1;
    }

    public void setSpare1(String spare1) {
        this.spare1 = spare1;
    }

    public String getSpare2() {
        return spare2;
    }

    public void setSpare2(String spare2) {
        this.spare2 = spare2;
    }

}
