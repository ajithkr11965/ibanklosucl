package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sib.ibanklosucl.dto.DkDataDTO;
import com.sib.ibanklosucl.dto.program.FDAccountDetails;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.*;
import com.sib.ibanklosucl.repository.program.FDAccountRepository;
import com.sib.ibanklosucl.repository.program.VehicleLoanFDRepository;
import com.sib.ibanklosucl.service.vlsr.FDAccountService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VLBREservice {

    @Autowired
    private VehicleLoanBlockService vehicleLoanBlockService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private VLBREparamsRepository vlbreparamsrepository;

    @Autowired
    private VehicleBRERepository vlBRERepository;

    @Autowired
    private VehicleEligiblityRepository vlEligiblityRepository;


    private final FDAccountRepository accountRepository;

    @Autowired
    private FDAccountService fdservice;

    @Autowired
    private VehicleLoanFDRepository vehicleLoanFDRepository;

    @Autowired
    private VehicleLoanLockRepository repository;

    @Autowired
    private DKDataRepository dkrepo;
    @Autowired
    private VehicleLoanEligibilityDetailsRepository vleligibilitydetailsrepo;

    @Autowired
    private iBankService iBankService;
    @Value("${app.dev-mode:true}")
    private boolean devMode;


    public VLBREservice(FDAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public String getAmberDatas(String wiNum, Long slno) throws Exception {
        try {
            List<VLBREparams> vlbreparams = vlbreparamsrepository.findByWinumAndSlnoOrderByApplicanttype(wiNum, slno);
            //System.out.println("Hai"+vlbreparams.toString());
            // log.info(" vlbreparams : {}  ",gson.toJson(vlbreparams));
            log.info(" vlbreparams data : {}    ~wiNum  {} ~ slno  {} ", vlbreparams.toString(), wiNum, slno);
            String eligibleDatas = getEligibleDatas(vlbreparams);
            JSONObject eligibleDatasObj = new JSONObject(eligibleDatas);
            if (eligibleDatasObj.getString("status").equals("success") && (vlbreparams.get(0).getQueue().equalsIgnoreCase("BC") || vlbreparams.get(0).getQueue().equalsIgnoreCase("RM"))) {
                JSONObject ltvjson = new JSONObject();
                JSONObject loanjson = new JSONObject();
                JSONObject tenurejson = new JSONObject();
                JSONObject agejson = new JSONObject();
                JSONObject bureaujson = new JSONObject();
                JSONObject amijson = new JSONObject();
                JSONObject editamountjson = new JSONObject();
                JSONObject huntercheckjson = new JSONObject();
                JSONObject polijson = new JSONObject();
                JSONObject dpdjson = new JSONObject();
                JSONObject racejson = new JSONObject();
                JSONObject totalbreempjson = new JSONObject();
                JSONObject currentbreempjson = new JSONObject();


                JSONArray loanjsonarray = new JSONArray();
                JSONArray tenorjsonarray = new JSONArray();
                JSONArray agejsonarray = new JSONArray();
                JSONArray bureaujsonarray = new JSONArray();
                JSONArray amijsonarray = new JSONArray();
                JSONArray huntercheckjsonarray = new JSONArray();
                JSONArray polijsonarray = new JSONArray();
                JSONArray dpdjsonarray = new JSONArray();
                JSONArray racejsonarray = new JSONArray();
                JSONArray totalbreempjsonarray = new JSONArray();
                JSONArray currentbreempjsonarray = new JSONArray();

                log.info("size {} ", vlbreparams.size());
                String ProgramName = "";
                if (vlbreparams.size() > 0) {
                    Map<String, List<VLBREparams>> applicantsByProgram = vlbreparams.stream()
                            .collect(Collectors.groupingBy(VLBREparams::getLoanprogram));
                    List<String> ProgramsName = applicantsByProgram.keySet().stream()
                            .filter(program -> !"NONE".equals(program)).collect(Collectors.toList());
                    if (!ProgramsName.isEmpty() && ProgramsName.size() == 1)
                        ProgramName = ProgramsName.get(0);
                }
                log.info("ProgramsName {} ", ProgramName);
                List<String> amberData = new ArrayList();
                List<String> mainamberData = new ArrayList();
                JSONObject returnJson = new JSONObject();

                String winum = vlbreparams.get(0).getWinum();
                Long serialno = vlbreparams.get(0).getSlno();
                String ltvtype = vlbreparams.get(0).getLtvtype();
                if (ltvtype == null) ltvtype = "";
                ltvjson.put("breCode", "AMB001");
                ltvjson.put("breDesc", "LTV% is manually entered");
                ltvjson.put("generic", "Y");
                if (ltvtype.equalsIgnoreCase("custom")) {
                    ltvjson.put("color", "amber");
                    mainamberData.add("amber");
                } else {
                    ltvjson.put("color", "green");
                    mainamberData.add("green");
                }
                /*************************** 2) loan amount section   ****************************************/
                String owner_applicant_id = String.valueOf(vlbreparams.get(0).getOwnerapplicantid());
                String first_time_buyer = vlbreparams.get(0).getFirsttimebuyer();
                if (first_time_buyer.equals("N")) {
                    for (VLBREparams vlbre : vlbreparams) {
                        String applicanttype = vlbre.getApplicanttype();
                        Long applicantid = vlbre.getId();
                        String pgm = vlbre.getLoanprogram();
                        if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                            pgm = ProgramName;
                        String emptype = vlbre.getEmptype();
                        String loanamount = vlbre.getEligibleloanamt();
                        String getColour = vlBRERepository.findloanamount(pgm, emptype, loanamount);
                        JSONObject loandata = new JSONObject(getColour);
                        loandata.put("applicantId", applicantid);
                        loandata.put("applicantType", applicanttype);
                        loandata.put("applicantName", vlbre.getApplname());
                        amberData.add(loandata.getString("color"));
                        loanjsonarray.put(loandata);
                    }
                } else if (first_time_buyer.equals("Y")) {
                    for (VLBREparams vlbre : vlbreparams) {
                        String applicanttype = vlbre.getApplicanttype();
                        Long applicantid = vlbre.getId();
                        String pgm = vlbre.getLoanprogram();
                        if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                            pgm = ProgramName;
                        String emptype = vlbre.getEmptype();
                        String loanamount = vlbre.getEligibleloanamt();
                        if (owner_applicant_id.equals(String.valueOf(applicantid))) {
                            String getColour = vlBRERepository.findfirsttimeloanamount(pgm, emptype, loanamount);
                            JSONObject loandata = new JSONObject(getColour);
                            loandata.put("applicantId", applicantid);
                            loandata.put("applicantType", applicanttype);
                            loandata.put("applicantName", vlbre.getApplname());
                            amberData.add(loandata.getString("color"));
                            loanjsonarray.put(loandata);
                        } else {
                            String getColour = vlBRERepository.findloanamount(pgm, emptype, loanamount);
                            JSONObject loandata = new JSONObject(getColour);
                            loandata.put("applicantId", applicantid);
                            loandata.put("applicantType", applicanttype);
                            loandata.put("applicantName", vlbre.getApplname());
                            amberData.add(loandata.getString("color"));
                            loanjsonarray.put(loandata);
                        }
                    }
                }
                loanjson.put("breCode", "AMB002");
                loanjson.put("breDesc", "Loan amount is outside STP range");
                loanjson.put("color", getMainColor(amberData));
                loanjson.put("generic", "N");
                loanjson.put("breSub", loanjsonarray);
                mainamberData.add(getMainColor(amberData));
                amberData.clear();

                /************************* 3) tenor section ****************************************/

                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    String tenure = vlbreparams.get(0).getTenor();
                    String getColour = vlBRERepository.findtenurecolour(pgm, emptype, tenure);
                    JSONObject tenordata = new JSONObject(getColour);
                    tenordata.put("applicantId", applicantid);
                    tenordata.put("applicantType", applicanttype);
                    tenordata.put("applicantName", vlbre.getApplname());
                    amberData.add(tenordata.getString("color"));
                    tenorjsonarray.put(tenordata);
                }
                tenurejson.put("breCode", "AMB007");
                tenurejson.put("breDesc", "Tenor is outside STP range");
                tenurejson.put("color", getMainColor(amberData));
                tenurejson.put("generic", "N");
                tenurejson.put("breSub", tenorjsonarray);
                mainamberData.add(getMainColor(amberData));
                amberData.clear();


                /********************** 4) MAX Age  section *********************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    if (applicanttype.equals("G"))
                        continue;
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    String retirementage = vlbre.getRetirementage();
                    String super_annuation = "", view_data = "";
                    Date applicantdob = vlbre.getApplicantdob();
                    String agecolor = null;
                    int flg2 = 1;

                    List<Map<String, String>> getAge = vlBRERepository.findmaxage(pgm, emptype);
                    log.info("getAge {}", getAge.size());
                    BigDecimal agePlusTenureNew =new BigDecimal(0) ;
                    if (!getAge.isEmpty() && getAge.size() == 1) {
                        log.info("getAge first {}", getAge.get(0));
                        log.info("getAge first 1 {}", getAge.get(0).get("SUPER_ANNUATION"));
                        log.info("getAge first 2 {}", getAge.get(0).get("MAX_AGE"));
                        if (getAge.get(0).get("SUPER_ANNUATION") != null && !getAge.get(0).get("SUPER_ANNUATION").equals("") && getAge.get(0).get("MAX_AGE") != null && !getAge.get(0).get("MAX_AGE").equals("")) {
                            if (applicantdob == null) {
                                agecolor = "amber";
                                view_data = "No Data";
                            } else {
                                LocalDate localDate = LocalDate.now();
                                LocalDate dob = applicantdob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                BigDecimal ageInYears = BigDecimal.valueOf(Period.between(dob, localDate).getYears());
                                BigDecimal ageInMonths= BigDecimal.valueOf(Period.between(dob, localDate).getMonths());
                                log.info("age in Years  : {}   and Months is  {}", ageInYears,ageInMonths);
                                BigDecimal monthsInYear = BigDecimal.valueOf(12);
                                BigDecimal totalAge = ageInYears.add(ageInMonths.divide(monthsInYear,2, RoundingMode.HALF_UP));
                                log.info("Actual age is   : {} ",totalAge);
                                int tenure = Integer.parseInt(vlbre.getTenor());
                                BigDecimal tenureInYears = BigDecimal.valueOf(tenure).divide(monthsInYear,2, RoundingMode.HALF_UP);
                                log.info("tenureInYears  is   : {} ",tenureInYears);
                                BigDecimal agePlusTenure = totalAge.add(tenureInYears);
                                agePlusTenureNew=agePlusTenure;
                                log.info("Final age is   : {} ",agePlusTenure);
                                if (getAge.get(0).get("SUPER_ANNUATION").equals("Y")) {
                                    if (retirementage == null) {
                                        agecolor = "amber";
                                        view_data = "No Data";
                                    } else {
                                        BigDecimal minage = new BigDecimal(Math.min(Integer.parseInt(getAge.get(0).get("MAX_AGE")), Integer.parseInt(retirementage)));
                                        log.info("minage  is   : {} ",minage);
                                        if (agePlusTenure.compareTo(minage) >=0) {
                                            agecolor = "amber";
                                            view_data = String.valueOf(minage);
                                        } else {
                                            agecolor = "green";
                                            view_data = String.valueOf(minage);
                                        }
                                    }
                                } else if (getAge.get(0).get("SUPER_ANNUATION").equals("N")) {
                                    BigDecimal minage = new BigDecimal(Integer.parseInt(getAge.get(0).get("MAX_AGE")));
                                    log.info("minage  is   : {} ",minage);
                                    if (agePlusTenure.compareTo(minage) >=0) {
                                        agecolor = "amber";
                                        view_data = getAge.get(0).get("MAX_AGE");
                                    } else {
                                        agecolor = "green";
                                        view_data = getAge.get(0).get("MAX_AGE");
                                    }

                                } else {
                                    agecolor = "amber";
                                    view_data = "No Data";
                                }
                            }

                        } else {
                            agecolor = "green";
                            view_data = "No Data";
                        }
                    } else {
                        agecolor = "green";
                        view_data = "No Data";
                    }

                    JSONObject agedata = new JSONObject();
                    agedata.put("applicantId", applicantid);
                    agedata.put("applicantType", applicanttype);
                    agedata.put("applicantName", vlbre.getApplname());
                    agedata.put("currentValue",agePlusTenureNew.intValue());
                    agedata.put("color", agecolor);
                    agedata.put("masterValue", view_data);
                    amberData.add(agecolor);
                    agejsonarray.put(agedata);
                }
                agejson.put("breCode", "AMB003");
                agejson.put("breDesc", "Maximum age limit breached");
                agejson.put("color", getMainColor(amberData));
                agejson.put("generic", "N");
                agejson.put("breSub", agejsonarray);
                mainamberData.add(getMainColor(amberData));
                amberData.clear();

                /************************** 5) bureau section *************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    Long bureauscore = vlbre.getBureauscore();
                    String getColour = vlBRERepository.findbureaucolour(pgm, emptype, bureauscore);
                    JSONObject bureaudata = new JSONObject(getColour);
                    bureaudata.put("applicantId", applicantid);
                    bureaudata.put("applicantType", applicanttype);
                    bureaudata.put("applicantName", vlbre.getApplname());
                    amberData.add(bureaudata.getString("color"));
                    bureaujsonarray.put(bureaudata);
                }
                bureaujson.put("breCode", "AMB004");
                bureaujson.put("breDesc", "Bureau score outside of STP range");
                bureaujson.put("color", getMainColor(amberData));
                bureaujson.put("generic", "N");
                bureaujson.put("breSub", bureaujsonarray);
                mainamberData.add(getMainColor(amberData));
                amberData.clear();
                /***************************** 6) Average Monthly income *************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String incomeconsidered = vlbre.getIncomeconsidered();
                    if (incomeconsidered.equals("Y")) {
                        String amitype = vlbre.getDoctype();
                        if (amitype == null) amitype = "";
                        String color = "";
                        JSONObject amidata = new JSONObject();
                        amidata.put("applicantId", applicantid);
                        amidata.put("applicantType", applicanttype);
                        amidata.put("applicantName", vlbre.getApplname());
                        if (!amitype.equalsIgnoreCase("70/30") && !amitype.equalsIgnoreCase("ITR") && !amitype.equalsIgnoreCase("ABB") && !amitype.equalsIgnoreCase("LOANFD"))
                            color = "amber";
                        else
                            color = "green";
                        amidata.put("color", color);
                        amidata.put("currentValue", amitype);
                        amidata.put("masterValue", "ITR or ABB or LOANFD or 70/30");
                        amberData.add(color);
                        amijsonarray.put(amidata);
                    }
                }

                amijson.put("breCode", "AMB012");
                amijson.put("breDesc", "Average monthly income is manually entered");
                amijson.put("generic", "N");
                amijson.put("color", getMainColor(amberData));
                amijson.put("breSub", amijsonarray);
                mainamberData.add(getMainColor(amberData));
                amberData.clear();
                /***************************** 7) Checker Edited Loan Amount *************/

                editamountjson.put("breCode", "AMB013");
                editamountjson.put("breDesc", "Branch checker has edited the recommended amount");
                editamountjson.put("generic", "Y");
                String editamount = vlbreparams.get(0).getLoanrecomentedamount();
                if (editamount == null) editamount = "0";
                String loanamount = vlbreparams.get(0).getEligibleloanamt();
                if (loanamount == null) loanamount = "0";
                BigDecimal editamount1 = new BigDecimal(editamount);
                BigDecimal loanamount1 = new BigDecimal(loanamount);
                if (editamount1.compareTo(loanamount1) != 0) {
                    editamountjson.put("color", "amber");
                    mainamberData.add("amber");
                } else {
                    editamountjson.put("color", "green");
                    mainamberData.add("green");
                }

                /***************************** 8) Hunter Check for Applicant **************/
            /*for (VLBREparams vlbre : vlbreparams) {
                String applicanttype = vlbre.getApplicanttype();
                Long applicantid = vlbre.getId();
                if(!vlbre.getApplicanttype().equals("G")) {
                    //vlbre.getDecision().equals("DECISION")
                    String hunterscore = vlbre.getHunterscore();
                    if (hunterscore == null) hunterscore = "0";
                    else hunterscore = hunterscore.trim();
                    String color = "";
                    JSONObject hunterdata = new JSONObject();
                    hunterdata.put("applicantId", applicantid);
                    hunterdata.put("applicantType", applicanttype);
                    hunterdata.put("applicantName", vlbre.getApplname());
                    int hscore = Integer.parseInt(hunterscore);
                    System.out.println("Hunter Score " + hscore);
                    if (hscore > 5)
                        color = "green";
                    else
                        color = "amber";
                    hunterdata.put("color", color);
                    hunterdata.put("currentValue", hunterscore);
                    hunterdata.put("masterValue", "5");
                    amberData.add(color);
                    huntercheckjsonarray.put(hunterdata);
                }
            }
            huntercheckjson.put("breCode", "AMB008");
            huntercheckjson.put("breDesc", "Hunter match ID is found");
            huntercheckjson.put("generic", "N");
            huntercheckjson.put("color", getMainColor(amberData));
            huntercheckjson.put("breSub", huntercheckjsonarray);
            mainamberData.add(getMainColor(amberData));
            amberData.clear();
            //COmmented
             */

                /**************** 9) POLITICALLY EXPOSED *********************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String politicaly_exposed = vlbre.getPoliticallyexposed();
                    if (politicaly_exposed == null) politicaly_exposed = "";
                    String color = "";
                    JSONObject polidata = new JSONObject();
                    polidata.put("applicantId", applicantid);
                    polidata.put("applicantType", applicanttype);
                    polidata.put("applicantName", vlbre.getApplname());
                    if (!politicaly_exposed.equalsIgnoreCase("NA"))
                        color = "amber";
                    else
                        color = "green";
                    polidata.put("color", color);
                    if (politicaly_exposed.equalsIgnoreCase("REP"))
                        polidata.put("currentValue", "Related to PEP");
                    else if (politicaly_exposed.equalsIgnoreCase("PEP"))
                        polidata.put("currentValue", "PEP (PoliticallyExposed Person)");
                    else
                        polidata.put("currentValue", politicaly_exposed);
                    polidata.put("masterValue", "Not Applicable");
                    amberData.add(color);
                    polijsonarray.put(polidata);
                }

                polijson.put("breCode", "AMB014");
                polijson.put("breDesc", "Politically Exposed / Relation to the Politically Exposed Persons");
                polijson.put("generic", "N");
                polijson.put("color", getMainColor(amberData));
                polijson.put("breSub", polijsonarray);
                mainamberData.add(getMainColor(amberData));
                amberData.clear();

                /***************************10) DPD Days  Section Start   ********************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String color = "";
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    Boolean stat = getDpdDaysStat(applicantid, pgm, emptype, vlbre.getSlno(), vlbre.getWinum(), "BC");
                    log.info("stat {} ", stat);
                    if (stat) {
                        color = "amber";
                    } else
                        color = "green";
                    JSONObject dpddata = new JSONObject();
                    dpddata.put("currentValue", "Not Applicable");
                    dpddata.put("color", color);
                    dpddata.put("masterValue", "Not Applicable");
                    dpddata.put("applicantId", applicantid);
                    dpddata.put("applicantType", applicanttype);
                    dpddata.put("applicantName", vlbre.getApplname());
                    dpdjsonarray.put(dpddata);
                    amberData.add(color);
                }
                if (dpdjsonarray.length() > 0) {
                    dpdjson.put("breCode", "AMB005");
                    dpdjson.put("breDesc", "DPD Days Found in Bureau Report ");
                    dpdjson.put("generic", "N");
                    dpdjson.put("color", getMainColor(amberData));
                    dpdjson.put("breSub", dpdjsonarray);
                    mainamberData.add(getMainColor(amberData));
                    amberData.clear();
                }
                /************************** 11) Race Score section *************************************/
                //New Changes
          //  if(devMode) {
                String table_name = "VLBUREAURACEBREMAP";
                String model_type = "NONKERALA";
                for (VLBREparams vlbre : vlbreparams) {
                    if (vlbre.getApplicanttype().equals("A")) {
                        if (vlbre.getState().equals("KL")) {
                            model_type = "KERALA";
                            break;
                        }
                    }
                }
                String breDesc = "RACE score outside of STP range";
                log.info("model_type {}     table_name  {} ", model_type, table_name);
                if (!ProgramName.equals("LOANFD")) {
                    for (VLBREparams vlbre : vlbreparams) {
                        String applicanttype = vlbre.getApplicanttype();
                        if (!applicanttype.equals("G")) {
                            Long applicantid = vlbre.getId();
                            String pgm = vlbre.getLoanprogram();
                            if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                                pgm = ProgramName;
                            String emptype = vlbre.getEmptype();
                            Long racescore = vlbre.getRacescore();
                            Long bureauscore = vlbre.getBureauscore();
                            String ntbflag = vlbre.getNtcflag();
                            if (ntbflag.equalsIgnoreCase("Y")) {
                                table_name = "VLNTBRACESLABMAS";
                                breDesc = "RACE score outside of STP range (NTC Present)";
                                log.info("table_name {} ", table_name);
                            }
                            String view_data = "";
                            List<Map<String, String>> getScoreMas = vlBRERepository.findracemasterdataNEW(pgm, table_name, model_type);
                            log.info("BRE RACE Green Master {} ", getScoreMas.toString());
                            String getColour = vlBRERepository.findracecolourNEW(pgm, emptype, racescore, table_name, bureauscore, model_type);
                            log.info("Colour is  {} ", getColour);
                            JSONObject racedata = new JSONObject(getColour);
                            racedata.put("applicantId", applicantid);
                            racedata.put("applicantType", applicanttype);
                            racedata.put("applicantName", vlbre.getApplname());
                            if (table_name.equals("VLNTBRACESLABMAS")) {
                                for (Map<String, String> Scoreval : getScoreMas) {
                                    view_data = view_data + " " + Scoreval.get("SCOREVIEW");
                                }
                            } else {
                                int i = 0;
                                for (Map<String, String> Scoreval : getScoreMas) {
                                    i++;
                                    view_data = view_data + i + ")" + Scoreval.get("SCOREVIEW") + ",";
                                }
                            }
                            racedata.put("masterValue", view_data);
                            log.info("Data is  {} ", racedata.toString());
                            amberData.add(racedata.getString("color"));
                            racejsonarray.put(racedata);
                        }
                    }
                } else {
                    log.info("FD Programe Race ");
                    for (VLBREparams vlbre : vlbreparams) {
                        String applicanttype = vlbre.getApplicanttype();
                        if (!applicanttype.equals("G")) {
                            Long applicantid = vlbre.getId();
                            String pgm = vlbre.getLoanprogram();
                            if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                                pgm = ProgramName;
                            Long racescore = vlbre.getRacescore();
                            Long bureauscore = vlbre.getBureauscore();
                            JSONObject racedata = new JSONObject();
                            racedata.put("applicantId", applicantid);
                            racedata.put("applicantType", applicanttype);
                            racedata.put("applicantName", vlbre.getApplname());
                            racedata.put("color", "green");
                            String current_data = "bureauscore :" + bureauscore + ",racescore :" + racescore;
                            racedata.put("currentValue", current_data);
                            racedata.put("masterValue", "0-1000");
                            amberData.add(racedata.getString("color"));
                            racejsonarray.put(racedata);
                        }
                    }
                }
                racejson.put("breCode", "AMB006");
                racejson.put("breDesc", breDesc);
                racejson.put("color", getRaceMainColor(amberData));
                racejson.put("generic", "N");
                racejson.put("breSub", racejsonarray);
                mainamberData.add(getRaceMainColor(amberData));
                amberData.clear();
           /* }else {   //Live  OLD METHOD

                String table_name = "VLNONKERALARACESLABMAS";
                for (VLBREparams vlbre : vlbreparams) {
                    if (vlbre.getApplicanttype().equals("A")) {
                        if (vlbre.getState().equals("KL")) {
                            table_name = "VLKERALARACESLABMAS";
                            break;
                        }
                    }
                }
                String breDesc = "RACE score outside of STP range";
                log.info("table_name {} ", table_name);
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    if (!applicanttype.equals("G")) {
                        Long applicantid = vlbre.getId();
                        String pgm = vlbre.getLoanprogram();
                        if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                            pgm = ProgramName;
                        String emptype = vlbre.getEmptype();
                        Long racescore = vlbre.getRacescore();
                        String ntbflag = vlbre.getNtcflag();
                        if (ntbflag.equalsIgnoreCase("Y")) {
                            table_name = "VLNTBRACESLABMAS";
                            breDesc = "RACE score outside of STP range (NTC Present)";
                            log.info("table_name {} ", table_name);
                        }
                        String view_data = "";
                        List<Map<String, String>> getScoreMas = vlBRERepository.findracemasterdata(pgm, table_name);
                        String getColour = vlBRERepository.findracecolour(pgm, emptype, racescore, table_name);
                        JSONObject racedata = new JSONObject(getColour);
                        racedata.put("applicantId", applicantid);
                        racedata.put("applicantType", applicanttype);
                        racedata.put("applicantName", vlbre.getApplname());
                        for (Map<String, String> Scoreval : getScoreMas) {
                            view_data = view_data + " " + Scoreval.get("SCOREVIEW");
                        }
                        racedata.put("masterValue", view_data);
                        amberData.add(racedata.getString("color"));
                        racejsonarray.put(racedata);
                    }
                }
                racejson.put("breCode", "AMB006");
                racejson.put("breDesc", breDesc);
                racejson.put("color", getRaceMainColor(amberData));
                racejson.put("generic", "N");
                racejson.put("breSub", racejsonarray);
                mainamberData.add(getRaceMainColor(amberData));
                amberData.clear();
            }*/

                /*************************** 15) Min total emp/bus section   ****************************************/

                    for (VLBREparams vlbre : vlbreparams) {
                        String applicanttype = vlbre.getApplicanttype();
                        String incomeconsidered = vlbre.getIncomeconsidered();
                        if (applicanttype.equals("G") || incomeconsidered.equals("N"))
                            continue;
                        Long applicantid = vlbre.getId();
                        String pgm = vlbre.getLoanprogram();
                        if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                            pgm = ProgramName;
                        String emptype = vlbre.getEmptype();
                        String totalemp = vlbre.getTotalexperience();
                        if (totalemp == null) totalemp = "0";
                        String getColour = vlBRERepository.findBreemploymentcolour(pgm, emptype, totalemp);
                        JSONObject totalempdata = new JSONObject(getColour);
                        totalempdata.put("applicantId", applicantid);
                        totalempdata.put("applicantType", applicanttype);
                        totalempdata.put("applicantName", vlbre.getApplname());
                        amberData.add(totalempdata.getString("color"));
                        totalbreempjsonarray.put(totalempdata);

                    }
                    totalbreempjson.put("breCode", "AMB015");
                    totalbreempjson.put("breDesc", "Total Employment is outside STP range");
                    totalbreempjson.put("color", getMainColor(amberData));
                    totalbreempjson.put("generic", "N");
                    totalbreempjson.put("breSub", totalbreempjsonarray);
                    mainamberData.add(getMainColor(amberData));
                    amberData.clear();

                    /*************************** 16) Min total current emp/bus section   ****************************************/
                    for (VLBREparams vlbre : vlbreparams) {
                        String applicanttype = vlbre.getApplicanttype();
                        String incomeconsidered = vlbre.getIncomeconsidered();
                        if (applicanttype.equals("G") || incomeconsidered.equals("N"))
                            continue;
                        Long applicantid = vlbre.getId();
                        String pgm = vlbre.getLoanprogram();
                        if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                            pgm = ProgramName;
                        String emptype = vlbre.getEmptype();
                        String totalcurremp = "";
                        if (emptype.equalsIgnoreCase("SALARIED") || emptype.equalsIgnoreCase("PENSIONER")) {
                            totalcurremp = vlbre.getCurrentexperience();
                        } else {
                            totalcurremp = vlbre.getCurrentbusinessexperience();
                        }
                        if (totalcurremp == null) totalcurremp = "0";
                        String getColour = vlBRERepository.findBreCurrentemploymentempcolour(pgm, emptype, totalcurremp);
                        log.info("getColour {} ", getColour);
                        JSONObject totalcurrempdata = new JSONObject(getColour);
                        totalcurrempdata.put("applicantId", applicantid);
                        totalcurrempdata.put("applicantType", applicanttype);
                        totalcurrempdata.put("applicantName", vlbre.getApplname());
                        amberData.add(totalcurrempdata.getString("color"));
                        currentbreempjsonarray.put(totalcurrempdata);
                    }
                    currentbreempjson.put("breCode", "AMB016");
                    currentbreempjson.put("breDesc", "Current Employment is outside STP range");
                    currentbreempjson.put("color", getMainColor(amberData));
                    currentbreempjson.put("generic", "N");
                    currentbreempjson.put("breSub", currentbreempjsonarray);
                    mainamberData.add(getMainColor(amberData));
                    amberData.clear();

                /*************************** 16) Min Total current emp/bus section end  ****************************************/




                returnJson.put("AMB001", ltvjson);
                returnJson.put("AMB002", loanjson);
                returnJson.put("AMB003", agejson);
                returnJson.put("AMB004", bureaujson);
                returnJson.put("AMB005", dpdjson);
                returnJson.put("AMB006", racejson);
                returnJson.put("AMB007", tenurejson);
                //returnJson.put("AMB008", huntercheckjson);
                returnJson.put("AMB012", amijson);
                returnJson.put("AMB013", editamountjson);
                returnJson.put("AMB014", polijson);
                returnJson.put("AMB015", totalbreempjson);
                returnJson.put("AMB016", currentbreempjson);

                JSONObject resp = new JSONObject();
                resp.put("status", "SUCCESS");
                resp.put("eligibilityFlag", "green");
                resp.put("breFlag", getMainColor(mainamberData));
                resp.put("breData", returnJson);
                log.info(" Final Resp  : {}  ", resp.toString());
                return resp.toString();


            } else {
                JSONObject resp = new JSONObject();
                resp.put("status", "SUCCESS");
                if (eligibleDatasObj.getString("status").equals("success"))
                    resp.put("eligibilityFlag", "green");
                else
                    resp.put("eligibilityFlag", "red");
                resp.put("eligibilityData", eligibleDatasObj);
                if (eligibleDatasObj.has("ELI000)")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI000", "General Program based Checks", "Program based Basic Checks");
                }
                if (eligibleDatasObj.has("ELI001")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI001", "Income/ABB Requirement", "The income or Average Bank Balance (ABB) must meet our minimum requirements to ensure you have sufficient financial capacity to repay the loan.");
                }
                if (eligibleDatasObj.has("ELI002")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI002", "Credit Score Range", "Your credit score must fall within our acceptable range. This score is a key indicator of creditworthiness and helps us assess lending risk.");
                }
                if (eligibleDatasObj.has("ELI003")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI003", "Loan Amount Range", "The requested loan amount must be within our stipulated range. This ensures the loan aligns with our lending policies and your financial situation.");
                }
                if (eligibleDatasObj.has("ELI004")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI004", "Loan Tenure Range", "The loan tenure (duration) must be within our acceptable range. This helps balance affordable monthly payments with a reasonable total cost of borrowing.");
                }
                if (eligibleDatasObj.has("ELI005")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI005", "Age Range Requirement", "The age of all applicants, co-applicants, and guarantors must be within our stipulated range to ensure legal compliance and manage lending risks.");
                }
                if (eligibleDatasObj.has("ELI006")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI006", "Minimum Age Requirement", "At least one applicant or co-applicant must meet our minimum age requirement. This ensures there's a primary borrower of legal age who can be held responsible for the loan.");
                }
                if (eligibleDatasObj.has("ELI007")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI007", "Minimum Employment/Business Experience", "The total employment or business experience must meet our minimum requirement. This helps us assess the stability of your income source.");
                }
                if (eligibleDatasObj.has("ELI008")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI008", "Minimum Current Employment Tenure", "Your current employment tenure must meet our minimum requirement. This helps us evaluate the stability of your current income source.");
                }
                if (eligibleDatasObj.has("ELI009")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI009", "Minimum Residence Duration", "The duration of stay in your current residence must meet our minimum requirement. This criterion helps us assess residential stability.");
                }
                if (eligibleDatasObj.has("ELI010")) {
                    addExplanationToEligibilityData(eligibleDatasObj, "ELI010", "KYC ID Number Matches Found between Applicants/Co-Applicants/Guarantors", "Same KYC ID Number is added for Applicants/Co-Applicants/Guarantors ");
                }
                resp.put("breData", "");
                log.info(" Final Resp  : {}  ", resp.toString());
                return resp.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
            JSONObject resp = new JSONObject();
            resp.put("status", "FAILURE");
            resp.put("eligibilityFlag", "red");
            resp.put("breFlag", "red");
            resp.put("breData","");
            log.info(" Exception Final Resp  : {}  ", resp.toString());
            return resp.toString();
        }


    }
    private void addExplanationToEligibilityData(JSONObject eligibilityData, String code, String title, String explanation) throws Exception {
        JSONObject criterionData = eligibilityData.getJSONObject(code);
        criterionData.put("title", title);
        criterionData.put("explanation", explanation);
    }


    public String getEligibleDatas(List<VLBREparams> vlbreparams) throws Exception {
        try {
            VehicleLoanEligibilityDetails vleligibilitydetails = new VehicleLoanEligibilityDetails();
            Gson gson= new Gson();
            String ipnutString = gson.toJson(vlbreparams);
            String ProgramName = "";
            JSONObject generaljson1 = new JSONObject();
            JSONObject idmatchjson = new JSONObject();
            JSONObject incomejson = new JSONObject();
            JSONObject loanjson = new JSONObject();
            JSONObject tenurejson = new JSONObject();
            JSONObject agejson = new JSONObject();
            JSONObject minagejson = new JSONObject();
            JSONObject bureaujson = new JSONObject();
            JSONObject totalempjson = new JSONObject();
            JSONObject totalcurrentempjson = new JSONObject();
            JSONObject stayjson = new JSONObject();
            JSONObject dpdjson = new JSONObject();

            JSONArray generaljsonarray = new JSONArray();
            JSONArray idmatchjsonarray = new JSONArray();
            JSONArray incomejsonarray = new JSONArray();
            JSONArray loanjsonarray = new JSONArray();
            JSONArray tenorjsonarray = new JSONArray();
            JSONArray agejsonarray = new JSONArray();
            JSONArray minagejsonarray = new JSONArray();
            JSONArray bureaujsonarray = new JSONArray();
            JSONArray totalempjsonarray = new JSONArray();
            JSONArray totalcurrentempjsonarray = new JSONArray();
            JSONArray stayjsonarray = new JSONArray();
            JSONArray dpdjsonarray = new JSONArray();
            log.info(" vlbreparams size : {}  ", vlbreparams.size());

            JSONObject returnJson = new JSONObject();
            if (vlbreparams.size() > 0) {
                /***************************************** Basic Validations ***********************************/
                String error_msg = "";
            /*boolean isNRIExistAndProgramIncome = vlbreparams.stream()
                    .anyMatch(applicant -> "N".equals(applicant.getResidentflg()) && "INCOME".equals(applicant.getLoanprogram())); */

                Map<String, List<VLBREparams>> applicantsByProgram = vlbreparams.stream()
                        .collect(Collectors.groupingBy(VLBREparams::getLoanprogram));
                List<String> ProgramsName = applicantsByProgram.keySet().stream()
                        .filter(program -> !"NONE".equals(program)).collect(Collectors.toList());
                if (!ProgramsName.isEmpty() && ProgramsName.size() == 1)
                    ProgramName = ProgramsName.get(0);
                log.info(" ProgramsName  : {}  ", ProgramName);
                long nonNoneProgramsCount = applicantsByProgram.keySet().stream()
                        .filter(program -> !"NONE".equals(program))
                        .count();
                long noneProgramsCount = applicantsByProgram.keySet().stream()
                        .filter(program -> "NONE".equals(program))
                        .count();
                long surrogateCount = applicantsByProgram.keySet().stream()
                        .filter("SURROGATE"::equals)
                        .count();
                long seventyThityCount = applicantsByProgram.keySet().stream()
                        .filter("70/30"::equals)
                        .count();
                long incomeConsideredCount = vlbreparams.stream()
                        .filter(program -> "Y".equals(program.getIncomeconsidered()))
                        .count();
                log.info(" incomeConsideredCount  : {}  ~  seventyThityCount {}  ~ surrogateCount {} ", incomeConsideredCount, seventyThityCount, surrogateCount);


                if (nonNoneProgramsCount == 0) {
                    List<String> nonNoneProgramsCountAppNames = vlbreparams.stream()
                            .filter(applicant -> !"NONE".equals(applicant.getLoanprogram()))
                            .map(VLBREparams::getApplname).collect(Collectors.toList());
                    JSONObject generaljson = new JSONObject();
                    generaljson.put("Desc", "At least one program other than 'NONE' should exist");
                    generaljson.put("color", "red");
                    generaljson.put("applicantName", nonNoneProgramsCountAppNames);
                    generaljsonarray.put(generaljson);
                }

                if (nonNoneProgramsCount > 1) {
                    List<String> nonNoneProgramsCountAppNames = vlbreparams.stream()
                            .filter(applicant -> !"NONE".equals(applicant.getLoanprogram()))
                            .map(VLBREparams::getApplname).collect(Collectors.toList());
                    JSONObject generaljson = new JSONObject();
                    generaljson.put("Desc", "All applicants should have either Same Program or None.Also At least  One person must have  program  other than None");
                    generaljson.put("color", "red");
                    generaljson.put("applicantName", nonNoneProgramsCountAppNames);
                    generaljsonarray.put(generaljson);
                }

                if (vlbreparams.stream().anyMatch(t -> !"NONE".equals(t.getLoanprogram()) && "G".equals(t.getApplicanttype()))) {
                    List<String> gurantorApplicantNames = vlbreparams.stream()
                            .filter(t -> !"NONE".equals(t.getLoanprogram()) && "G".equals(t.getApplicanttype()))
                            .map(VLBREparams::getApplname).collect(Collectors.toList());
                    JSONObject generaljson = new JSONObject();
                    generaljson.put("Desc", "Guarantors Program Type Should be NONE");
                    generaljson.put("color", "red");
                    generaljson.put("applicantName", gurantorApplicantNames);
                    generaljsonarray.put(generaljson);
                }
                /******************************************** Surrogate Check **********************************************/

                if (surrogateCount > 1) {
                    JSONObject generaljson = new JSONObject();
                    generaljson.put("Desc", "Surrogate Program should be chosen only once!!");
                    generaljson.put("color", "red");
                    generaljsonarray.put(generaljson);
                }
                if (surrogateCount == 1 && incomeConsideredCount > 1) {
                    JSONObject generaljson = new JSONObject();
                    generaljson.put("Desc", "For Surrogate Program ,Income Considered should be chosen only once!!");
                    generaljson.put("color", "red");
                    generaljsonarray.put(generaljson);
                }

                boolean isSurrogateProgram = vlbreparams.stream()
                        .anyMatch(applicant -> "SURROGATE".equals(applicant.getLoanprogram()));
                if (surrogateCount == 1 && isSurrogateProgram) {
                    boolean isNRIExistAndProgramSurogate = vlbreparams.stream()
                            .anyMatch(applicant -> "N".equals(applicant.getResidentflg())  && "Y".equals(applicant.getIncomeconsidered())  && ("SURROGATE".equals(applicant.getLoanprogram()) || "NONE".equals(applicant.getLoanprogram())));
                    log.info(" isNRIExistAndProgramSurogate  : {}  ", isNRIExistAndProgramSurogate);
                    if (isNRIExistAndProgramSurogate) {
                        List<String> surrogateApplicantNames = vlbreparams.stream()
                                .filter(applicant -> "N".equals(applicant.getResidentflg()))
                                .map(VLBREparams::getApplname).collect(Collectors.toList());
                        JSONObject generaljson = new JSONObject();
                        generaljson.put("Desc", "For Surrogate , NRI is not allowed");
                        generaljson.put("color", "red");
                        generaljson.put("applicantName", surrogateApplicantNames);
                        generaljsonarray.put(generaljson);
                    }
                }


                boolean NRIPresent = vlbreparams.stream().anyMatch
                        (applicant -> "N".equals(applicant.getResidentflg()));
                if (NRIPresent) {
                    boolean residentBorrower = vlbreparams.stream().anyMatch
                            (applicant -> "R".equals(applicant.getResidentflg()));
                    if (!residentBorrower) {
                        boolean isFDProgramName = vlbreparams.stream()
                                .anyMatch(applicant -> "LOANFD".equals(applicant.getLoanprogram()));
                       // if(!isFDProgramName) {
                            List<String> residentBorrowerNames = vlbreparams.stream()
                                    .filter(applicant -> "R".equals(applicant.getResidentflg()))
                                    .map(VLBREparams::getApplname).collect(Collectors.toList());
                            JSONObject generaljson = new JSONObject();
                            generaljson.put("Desc", "if NRI Applicant/Co Applicant/Guarantor  is available , At least one Resident should be joined ");
                            generaljson.put("color", "red");
                            generaljson.put("applicantName", residentBorrowerNames);
                            generaljsonarray.put(generaljson);
                       // }
                    }
                }


                if (vlbreparams.get(0).getFoirtype().equals("N")) {
                    boolean NRIAppCoAppIncomeConsidered = vlbreparams.stream().anyMatch
                            (applicant -> "N".equals(applicant.getResidentflg()) && "Y".equals(applicant.getIncomeconsidered()));
                    if (NRIAppCoAppIncomeConsidered) {
                        boolean isIncomeProgram = vlbreparams.stream()
                                .anyMatch(applicant -> "INCOME".equals(applicant.getLoanprogram()));
                        if (isIncomeProgram) {
                            List<String> NRIAppCoAppNames = vlbreparams.stream()
                                    .filter(applicant -> "N".equals(applicant.getResidentflg()) && ("A".equals(applicant.getApplicanttype()) || "C".equals(applicant.getApplicanttype())))
                                    .map(VLBREparams::getApplname).collect(Collectors.toList());
                            JSONObject generaljson = new JSONObject();
                            generaljson.put("Desc", "NRI with Income considered Yes is not allowed to select Non -FOIR in INCOME Program ");
                            generaljson.put("color", "red");
                            generaljson.put("applicantName", NRIAppCoAppNames);
                            generaljsonarray.put(generaljson);
                        }
                    }
                }

                String programName = String.valueOf(applicantsByProgram.keySet().stream()
                        .filter(program -> !"NONE".equals(program))
                        .findFirst());
                log.info(" noneProgramsCount  : {}  ", noneProgramsCount);
                /******************************************** 70/30 Check **********************************************/
                boolean is7030Program = vlbreparams.stream()
                        .anyMatch(applicant -> "70/30".equals(applicant.getLoanprogram()));


                if (is7030Program && seventyThityCount == 1) {
                    boolean is7030ProgramAndNoneEmployment = vlbreparams.stream()
                            .anyMatch(applicant -> "NONE".equals(applicant.getEmptype()) && "70/30".equals(applicant.getLoanprogram()));
                    if (is7030ProgramAndNoneEmployment) {
                        List<String> noneApplicantNames = vlbreparams.stream()
                                .filter(applicant -> "NONE".equals(applicant.getEmptype()) && "70/30".equals(applicant.getLoanprogram()))
                                .map(VLBREparams::getApplname).collect(Collectors.toList());
                        JSONObject generaljson = new JSONObject();
                        generaljson.put("Desc", "Employment Type NONE should not be selected for 70/30 Program");
                        generaljson.put("color", "red");
                        generaljson.put("applicantName", noneApplicantNames);
                        generaljsonarray.put(generaljson);
                    }
                    boolean is7030ProgramAndNoCoapplicant = vlbreparams.stream()
                            .anyMatch(applicant -> ("70/30".equals(applicant.getLoanprogram()) || "NONE".equals(applicant.getLoanprogram())) && ("G".equals(applicant.getApplicanttype()) || "C".equals(applicant.getApplicanttype())));

                    if (!is7030ProgramAndNoCoapplicant) {
                        List<String> ApplicantNames = vlbreparams.stream()
                                .filter(applicant -> "70/30".equals(applicant.getLoanprogram()))
                                .map(VLBREparams::getApplname).collect(Collectors.toList());
                        JSONObject generaljson = new JSONObject();
                        generaljson.put("Desc", "For 70/30 Program,Atleast One Co Applicant/Guarantor must be required ");
                        generaljson.put("color", "red");
                        generaljson.put("applicantName", ApplicantNames);
                        generaljsonarray.put(generaljson);
                    } else {  // Need to Put married Check
                        boolean is7030ProgramMarriedApplicant = vlbreparams.stream()
                                .anyMatch(applicant -> "A".equals(applicant.getApplicanttype()) && "MARID".equals(applicant.getMaritalstatus()));
                        if (is7030ProgramMarriedApplicant) {
                            boolean is7030ProgramSpouseCheck = vlbreparams.stream()
                                    .anyMatch(applicant -> ("C".equals(applicant.getApplicanttype()) || "G".equals(applicant.getApplicanttype())) && ("001".equals(applicant.getRelationwithapplicant()) || "002".equals(applicant.getRelationwithapplicant())));
                            if (!is7030ProgramSpouseCheck) {
                                List<String> ApplicantNames = vlbreparams.stream()
                                        .filter(applicant -> ("C".equals(applicant.getApplicanttype()) || "G".equals(applicant.getApplicanttype())))
                                        .map(VLBREparams::getApplname).collect(Collectors.toList());
                                JSONObject generaljson = new JSONObject();
                                generaljson.put("Desc", "Spouse should be added as Co Applicant/Guarantor , if Applicant is Married in 70/30 Program. ");
                                generaljson.put("color", "red");
                                generaljson.put("applicantName", ApplicantNames);
                                generaljsonarray.put(generaljson);
                            }
                        } else {
                            boolean is7030ProgramOthersCheck = vlbreparams.stream()
                                    .anyMatch(applicant -> ("C".equals(applicant.getApplicanttype()) || "G".equals(applicant.getApplicanttype())) && "7".equals(applicant.getRelationwithapplicant()));
                            if (is7030ProgramOthersCheck) {
                                List<String> ApplicantNames = vlbreparams.stream()
                                        .filter(applicant -> ("C".equals(applicant.getApplicanttype()) || "G".equals(applicant.getApplicanttype())) && "7".equals(applicant.getRelationwithapplicant()))
                                        .map(VLBREparams::getApplname).collect(Collectors.toList());
                                JSONObject generaljson = new JSONObject();
                                generaljson.put("Desc", "For UnMarried Applicant, Close Relatives only allowed in Co Applicant/Guarantor in 70/30 Program. (Others Option shouldn't be selected in Relationship with Applicant) ");
                                generaljson.put("color", "red");
                                generaljson.put("applicantName", ApplicantNames);
                                generaljsonarray.put(generaljson);
                            }

                        }

                    }
                }

                /******************************************** FD Check **********************************************/
                boolean isFDProgram = vlbreparams.stream()
                        .anyMatch(applicant -> "LOANFD".equals(applicant.getLoanprogram()));
                if (isFDProgram) {
                    List<String> missedCIFIds = fdservice.getMissingCifIds(vlbreparams.get(0).getWinum());
                    log.info(" missedCIFIds  : {}  ", missedCIFIds.toString());
                    if (missedCIFIds.size() > 0) {
                        JSONObject generaljson = new JSONObject();
                        generaljson.put("Desc", "Kindly add all joint holders of Fixed deposit as Co Applicants.Missed CIF IDs :" + missedCIFIds.toString());
                        generaljson.put("color", "red");
                        // generaljson.put("applicantName", );
                        generaljsonarray.put(generaljson);
                    }
                    vlbreparams.stream().forEach(applicant -> {
                        log.info("Cif id {} ", applicant.getCifid());
                        if (applicant.getLoanprogram().equals("LOANFD")) {
                            BigDecimal sumAmount = BigDecimal.valueOf(0.0);
                            BigDecimal val = new BigDecimal(applicant.getDepamt());
                            List<FDAccountDetails> accountDetailsList = accountRepository.findAccountDetails(applicant.getCifid());
                            List<VehicleLoanFD> amountMismatchFdAccounts = new ArrayList<>();
                            List<VehicleLoanFD> nonfoundFdAccounts = new ArrayList<>();
                            if (accountDetailsList != null && !accountDetailsList.isEmpty()) {
                                List<VehicleLoanFD> eligibleFDs = vehicleLoanFDRepository.findByApplicantIdAndWiNumAndEligibleAndDelFlg(applicant.getId(), applicant.getWinum(), true, "N");
                                if (eligibleFDs.isEmpty()) {
                                    JSONObject generaljson = new JSONObject();
                                    generaljson.put("Desc", "FD Accounts are not selected in los.Kindly add Fd Accounts in Program Details Once Again");
                                    generaljson.put("color", "red");
                                    generaljson.put("applicantName", applicant.getApplname());
                                    generaljsonarray.put(generaljson);
                                } else {
                                    for (VehicleLoanFD eligbleDetails : eligibleFDs) {
                                        Optional<FDAccountDetails> masterOpt = accountDetailsList.stream()
                                                .filter(accountDetails -> accountDetails.getFdAccNo().equals(eligbleDetails.getFdaccnum()))
                                                .findFirst();

                                        if (!masterOpt.isPresent()) {
                                            log.info("elgible cif id {} ", eligbleDetails.getCifid());
                                            nonfoundFdAccounts.add(eligbleDetails);
                                        } else {
                                            BigDecimal computedAvailablefdBalance = masterOpt.get().getFdAmount().subtract(masterOpt.get().getFsldAdjAmount());
                                            if (computedAvailablefdBalance.compareTo(eligbleDetails.getAvailbalance())!=0) {
                                                amountMismatchFdAccounts.add(eligbleDetails);
                                            } else
                                                sumAmount = sumAmount.add(eligbleDetails.getAvailbalance());
                                        }
                                    }
                                    sumAmount=sumAmount.setScale(2,RoundingMode.HALF_UP);
                                }
                                log.info("nonfoundFdAccounts size  {} ", nonfoundFdAccounts.size());
                                log.info("nonfoundFdAccounts {}", nonfoundFdAccounts);
                                if (!nonfoundFdAccounts.isEmpty()) {
                                    JSONObject generaljson = new JSONObject();
                                    generaljson.put("Desc", "FD Accounts are not available in Finacle. " + nonfoundFdAccounts.stream().map(VehicleLoanFD::getFdaccnum).collect(Collectors.joining(", ")) + ".Kindly Save Program Details Once Again");
                                    generaljson.put("color", "red");
                                    generaljson.put("applicantName", applicant.getApplname());
                                    generaljsonarray.put(generaljson);
                                }
                                if (!amountMismatchFdAccounts.isEmpty()) {
                                    JSONObject generaljson = new JSONObject();
                                    generaljson.put("Desc", "Amount Mismatch found with Finacle Fd Account. " + amountMismatchFdAccounts.stream().map(VehicleLoanFD::getFdaccnum).collect(Collectors.joining(", ")) + ".Kindly Save Program Details Once Again");
                                    generaljson.put("color", "red");
                                    generaljson.put("applicantName", applicant.getApplname());
                                    generaljsonarray.put(generaljson);
                                }
                                if (val.compareTo(sumAmount) != 0) {
                                    JSONObject generaljson = new JSONObject();
                                    generaljson.put("Desc", "Mismatch found in Total Deposit Amount ( Finacle Value : " + sumAmount + " , LOS Value : " + val + " ).Kindly Save Program Details Once Again");
                                    generaljson.put("color", "red");
                                    generaljson.put("applicantName", applicant.getApplname());
                                    generaljsonarray.put(generaljson);
                                }
                            } else {
                                JSONObject generaljson = new JSONObject();
                                generaljson.put("Desc", "Mismatch found in Deposit Amount ( Finacle Value : " + sumAmount + " , LOS Value : " + val + " ).Kindly Save Program Details Once Again");
                                generaljson.put("color", "red");
                                generaljson.put("applicantName", applicant.getApplname());
                                generaljsonarray.put(generaljson);
                            }
                        }
                    });

                }

                boolean isValidProgramAndNoneEmploymentwithIncomeConsidered = vlbreparams.stream()
                        .anyMatch(applicant -> "NONE".equals(applicant.getEmptype()) && ("INCOME".equals(applicant.getLoanprogram()) || "70/30".equals(applicant.getLoanprogram()) || "SURROGATE".equals(applicant.getLoanprogram())) && "Y".equals(applicant.getIncomeconsidered()) );
                if (isValidProgramAndNoneEmploymentwithIncomeConsidered) {
                    List<String> noneApplicantNames = vlbreparams.stream()
                            .filter(applicant -> "NONE".equals(applicant.getEmptype()) && !"NONE".equals(applicant.getLoanprogram()) && "Y".equals(applicant.getIncomeconsidered()) )
                            .map(VLBREparams::getApplname).collect(Collectors.toList());
                    JSONObject generaljson = new JSONObject();
                    generaljson.put("Desc", "Employment Type NONE should not be selected for Income Considered Yes Cases");
                    generaljson.put("color", "red");
                    generaljson.put("applicantName", noneApplicantNames);
                    generaljsonarray.put(generaljson);
                }

                vlbreparams.stream().forEach(applicant -> {
                    log.info("getCifcreationmode {}", applicant.getCifcreationmode());
                });
                Set<String> cifCreationModeMatchList = vlbreparams.stream().filter(t -> "N".equals(t.getSibcustomer()) && !"NA".equalsIgnoreCase(t.getCifcreationmode()))
                        .map(VLBREparams::getCifcreationmode)
                        .collect(Collectors.toSet());
                log.info("cifCreationModeMatchList  {}  ~  Size {} ", cifCreationModeMatchList.toString(), cifCreationModeMatchList.size());
                if (cifCreationModeMatchList.size() > 1) {
                    JSONObject generaljson = new JSONObject();
                    generaljson.put("Desc", "All New To Bank Applicants should have Same Customer ID Creation Mode.kindly Change Customer Creation Mode in KYC Tab!!");
                    generaljson.put("color", "red");
                    generaljsonarray.put(generaljson);
                }

                vlbreparams.stream().forEach(applicant -> {
                    if (!"Y".equals(applicant.getGencomplete()) || !"Y".equals(applicant.getKyccomplete()) || !"Y".equals(applicant.getIncomecomplete()) || !"Y".equals(applicant.getBasiccomplete()) || !"Y".equals(applicant.getCreditcomplete()) || !"Y".equals(applicant.getEmploymentcomplete())) {
                        String data = " Incomplete Sections ";
                        if (!"Y".equals(applicant.getGencomplete()))
                            data = data + "  General Details ";
                        if (!"Y".equals(applicant.getKyccomplete()))
                            data = data + "  KYC Details ";
                        if (!"Y".equals(applicant.getIncomecomplete()))
                            data = data + "  Program Details ";
                        if (!"Y".equals(applicant.getBasiccomplete()))
                            data = data + "  Basic Details ";
                        if (!"Y".equals(applicant.getCreditcomplete()))
                            data = data + "  Credit Details ";
                        if (!"Y".equals(applicant.getEmploymentcomplete()))
                            data = data + "  Employment Details ";
                        JSONObject generaljson = new JSONObject();
                        try {
                            generaljson.put("Desc", data);
                            generaljson.put("color", "red");
                            generaljson.put("applicantName", applicant.getApplname());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        generaljsonarray.put(generaljson);
                    }
                    if (applicant.getApplicanttype().equalsIgnoreCase("A") && (!"Y".equals(applicant.getVehiclecomplete()) || !"Y".equals(applicant.getLoancomplete()))) {
                        String data1 = " Kindly Resubmit ";
                        int flg2 = 0;
                        String data = " Incomplete Sections ";
                        if (!applicant.getVehiclecomplete().equals("Y")) {
                            data = data + "  Vehicle Details ";
                            flg2 = 1;
                        }
                        if (!applicant.getLoancomplete().equals("Y") && applicant.getFoirtype() == null) {
                            data = data + "  Loan Details ";
                            flg2 = 1;
                        } else if (!applicant.getLoancomplete().equals("Y") && applicant.getFoirtype() != null) {
                            if (flg2 == 0) {
                                data = data1 + " Loan Details ";
                            } else {
                                data = data + "  Loan Details ";
                            }

                        }
                        JSONObject generaljson = new JSONObject();
                        try {
                            generaljson.put("Desc", data);
                            generaljson.put("color", "red");
                            generaljson.put("applicantName", applicant.getApplname());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        generaljsonarray.put(generaljson);
                    }
                });
                int redflg = 0;
                if (generaljsonarray.length() > 0) {
                    generaljson1.put("eliCode", "ELI000");
                    generaljson1.put("eliDesc", "General Conditions");
                    generaljson1.put("color", "red");
                    generaljson1.put("generic", "N");
                    generaljson1.put("eliSub", generaljsonarray);
                    redflg = 1;
                }
                /*************************** KYC Check   ****************************************/
                Map<String, List<String>> panMatchList = vlbreparams.stream().filter(t -> !"NA".equalsIgnoreCase(t.getPanno()))
                        .collect(Collectors.groupingBy(VLBREparams::getPanno)).entrySet().stream().filter(entry -> entry.getValue().size() > 1)
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(VLBREparams::getApplname).collect(Collectors.toList())));

                Map<String, List<String>> aadharMatchList = vlbreparams.stream().filter(t -> !"NA".equalsIgnoreCase(t.getAadharrefnum()))
                        .collect(Collectors.groupingBy(VLBREparams::getAadharrefnum)).entrySet().stream().filter(entry -> entry.getValue().size() > 1)
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(VLBREparams::getApplname).collect(Collectors.toList())));

                Map<String, List<String>> visaOCIMatchList = vlbreparams.stream().filter(t -> !"NA".equalsIgnoreCase(t.getVisaocinumber()))
                        .collect(Collectors.groupingBy(VLBREparams::getVisaocinumber)).entrySet().stream().filter(entry -> entry.getValue().size() > 1)
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(VLBREparams::getApplname).collect(Collectors.toList())));

                Map<String, List<String>> PassportMatchList = vlbreparams.stream().filter(t -> !"NA".equalsIgnoreCase(t.getPassportnumber()))
                        .collect(Collectors.groupingBy(VLBREparams::getPassportnumber)).entrySet().stream().filter(entry -> entry.getValue().size() > 1)
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(VLBREparams::getApplname).collect(Collectors.toList())));

                if (!panMatchList.isEmpty()) {
                    panMatchList.forEach((panNumber, applicantNames) -> {
                        JSONObject panmatchdata = new JSONObject();
                        panmatchdata.put("masterValue", "pan Number");
                        panmatchdata.put("currentValue", panNumber);
                        panmatchdata.put("applicantName", new JSONArray(applicantNames));
                        idmatchjsonarray.put(panmatchdata);
                    });
                }
                if (!aadharMatchList.isEmpty()) {
                    aadharMatchList.forEach((aadharNumber, applicantNames) -> {
                        JSONObject aadharmatchdata = new JSONObject();
                        aadharmatchdata.put("masterValue", "Aadhaar Number");
                        aadharmatchdata.put("currentValue", aadharNumber);
                        aadharmatchdata.put("applicantName", new JSONArray(applicantNames));
                        idmatchjsonarray.put(aadharmatchdata);
                    });
                }
                if (!visaOCIMatchList.isEmpty()) {
                    visaOCIMatchList.forEach((visaOCINumber, applicantNames) -> {
                        JSONObject visaOCINumbermatchdata = new JSONObject();
                        visaOCINumbermatchdata.put("masterValue", "VISA/OCI Number");
                        visaOCINumbermatchdata.put("currentValue", visaOCINumber);
                        visaOCINumbermatchdata.put("applicantName", new JSONArray(applicantNames));
                        idmatchjsonarray.put(visaOCINumbermatchdata);
                    });
                }
                if (!PassportMatchList.isEmpty()) {
                    PassportMatchList.forEach((PassportNumber, applicantNames) -> {
                        JSONObject Passportmatchdata = new JSONObject();
                        Passportmatchdata.put("masterValue", "Passport Number");
                        Passportmatchdata.put("currentValue", PassportNumber);
                        Passportmatchdata.put("applicantName", new JSONArray(applicantNames));
                        idmatchjsonarray.put(Passportmatchdata);
                    });
                }

                if (idmatchjsonarray.length() > 0) {
                    idmatchjson.put("eliCode", "ELI010");
                    idmatchjson.put("eliDesc", "KYC ID Number Matches Found");
                    idmatchjson.put("color", "red");
                    idmatchjson.put("generic", "N");
                    idmatchjson.put("eliSub", idmatchjsonarray);
                    redflg = 1;
                }


                /*************************** 1) KYC Section ends   ****************************************/
                /*************************** 1) Income amount section   ****************************************/
                int atleasetoneeligibleIncome = 0;
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    if (applicanttype.equals("G"))
                        continue;
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    String incomeconsidered = vlbre.getIncomeconsidered();
                    if (incomeconsidered.equals("Y")) {
                        String color = "";
                        String income = "";
                        String view_data = "";
                        if (pgm.equalsIgnoreCase("INCOME"))
                            income = vlbre.getAvgsal();
                        else if (pgm.equalsIgnoreCase("SURROGATE"))
                            income = vlbre.getAbb();
                        else if (pgm.equalsIgnoreCase("LOANFD"))
                            income = vlbre.getDepamt();
                        log.info(" income  : {}  ", income);
                        String getColour = vlEligiblityRepository.findEligibleMinIncomecolour(pgm, emptype, income);
                        JSONObject incomedata = new JSONObject(getColour);
                        if (incomedata.get("color").equals("red")) {
                            incomedata.put("applicantId", applicantid);
                            incomedata.put("applicantType", applicanttype);
                            incomedata.put("applicantName", vlbre.getApplname());
                            incomejsonarray.put(incomedata);
                        } else {
                            atleasetoneeligibleIncome = 1;
                            break;
                        }
                    }
                }
                if (atleasetoneeligibleIncome == 0) {
                    incomejson.put("eliCode", "ELI001");
                    incomejson.put("eliDesc", "Income/ABB criterion each applicant/co-appl whose income considered=Yes is not met");
                    incomejson.put("color", "red");
                    incomejson.put("generic", "N");
                    incomejson.put("eliSub", incomejsonarray);
                    redflg = 1;
                }
                /*************************** 1) Income amount section end  ****************************************/


                /*************************** 2) Bureau score section   ****************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    Long score = vlbre.getBureauscore();
                    List<Map<String, String>> getScore = vlEligiblityRepository.findscoredata(pgm);
                    log.info("Score {}", getScore.toString());
                    if (!getScore.isEmpty() && getScore.size() >= 1) {
                        String getColour = vlEligiblityRepository.findEligibleBureaucolour(pgm, emptype, String.valueOf(score));
                        JSONObject scoredata = new JSONObject(getColour);
                        if (scoredata.get("color").equals("red")) {
                            scoredata.put("applicantId", applicantid);
                            scoredata.put("applicantType", applicanttype);
                            scoredata.put("applicantName", vlbre.getApplname());
                            String view_data = "";
                            for (Map<String, String> Scoreval : getScore) {
                                if (Scoreval.get("COLOR").equals("red")) {
                                    view_data = view_data + " " + Scoreval.get("SCOREVIEW");
                                }
                            }
                            scoredata.put("masterValue", view_data);
                            bureaujsonarray.put(scoredata);
                        }

                    }
                }

                if (bureaujsonarray.length() > 0) {
                    bureaujson.put("eliCode", "ELI002");
                    bureaujson.put("eliDesc", "Bureau score of all applicants/co-app and guarantors not within the stipulated range");
                    bureaujson.put("color", "red");
                    bureaujson.put("generic", "N");
                    bureaujson.put("eliSub", bureaujsonarray);
                    redflg = 1;
                }
                /*************************** 2) Bureau score section end  ****************************************/


                /*************************** 3) Loan amount section   ****************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    String loanamt = "";
                    if (vlbreparams.get(0).getQueue().equalsIgnoreCase("BC") || vlbreparams.get(0).getQueue().equalsIgnoreCase("RM"))
                        loanamt = vlbre.getEligibleloanamt();
                    else
                        loanamt = vlbre.getLoanamount();
                    log.info("Eligible loanamt {} ", loanamt);
                    String getColour = vlEligiblityRepository.findEligibleLoanAmountcolour(pgm, emptype, loanamt);
                    JSONObject loandata = new JSONObject(getColour);
                    if (loandata.get("color").equals("red")) {
                        loandata.put("applicantId", applicantid);
                        loandata.put("applicantType", applicanttype);
                        loandata.put("applicantName", vlbre.getApplname());
                        loanjsonarray.put(loandata);
                    }
                }
                if (loanjsonarray.length() > 0) {
                    loanjson.put("eliCode", "ELI003");
                    loanjson.put("eliDesc", "Loan amount not within the stipulated range");
                    loanjson.put("color", "red");
                    loanjson.put("generic", "N");
                    loanjson.put("eliSub", loanjsonarray);
                    redflg = 1;
                }
                /*************************** 3) Loan amount section end  ****************************************/

                /*************************** 4) Tunure section   ****************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    String tenure = vlbre.getTenor();
                    String getColour = vlEligiblityRepository.findEligibletenurecolour(pgm, emptype, tenure);
                    JSONObject tenuredata = new JSONObject(getColour);
                    if (tenuredata.get("color").equals("red")) {
                        tenuredata.put("applicantId", applicantid);
                        tenuredata.put("applicantType", applicanttype);
                        tenuredata.put("applicantName", vlbre.getApplname());
                        tenorjsonarray.put(tenuredata);
                    }

                }
                if (tenorjsonarray.length() > 0) {
                    tenurejson.put("eliCode", "ELI004");
                    tenurejson.put("eliDesc", "Loan tenor not within the stipulated range");
                    tenurejson.put("color", "red");
                    tenurejson.put("generic", "N");
                    tenurejson.put("eliSub", tenorjsonarray);
                    redflg = 1;
                }
                /*************************** 4) Tenure section end  ****************************************/

                /*************************** 5) Age section   ****************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    int tenure = Integer.parseInt(vlbre.getTenor());
                    BigDecimal agePlusTenureNew =new BigDecimal(0) ;
                    if (vlbre.getApplicantdob() != null) {
                        //age = Period.between(vlbre.getApplicantdob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
                        LocalDate localDate = LocalDate.now();
                        LocalDate dob = vlbre.getApplicantdob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        BigDecimal ageInYears = BigDecimal.valueOf(Period.between(dob, localDate).getYears());
                        BigDecimal ageInMonths= BigDecimal.valueOf(Period.between(dob, localDate).getMonths());
                        log.info("age in Years  : {}   and Months is  {}", ageInYears,ageInMonths);
                        BigDecimal monthsInYear = BigDecimal.valueOf(12);
                        BigDecimal totalAge = ageInYears.add(ageInMonths.divide(monthsInYear,2, RoundingMode.HALF_UP));
                        log.info("Actual age is   : {} ",totalAge);
                        BigDecimal tenureInYears = BigDecimal.valueOf(tenure).divide(monthsInYear,2, RoundingMode.HALF_UP);
                        log.info("tenureInYears  is   : {} ",tenureInYears);
                        BigDecimal agePlusTenure = totalAge.add(tenureInYears);
                        agePlusTenureNew=agePlusTenure;
                        log.info("Final age is   : {} ",agePlusTenure);
                        String getColour = vlEligiblityRepository.findEligibleageecolour(pgm, emptype, agePlusTenure);
                        JSONObject agedata = new JSONObject(getColour);
                        if (agedata.get("color").equals("red")) {
                            agedata.put("applicantId", applicantid);
                            agedata.put("applicantType", applicanttype);
                            agedata.put("applicantName", vlbre.getApplname());
                            agejsonarray.put(agedata);
                        }
                    }
                }
                if (agejsonarray.length() > 0) {
                    agejson.put("eliCode", "ELI005");
                    agejson.put("eliDesc", "Age of all applicants, co-applicants and guarantors not within the stipulated range");
                    agejson.put("color", "red");
                    agejson.put("generic", "N");
                    agejson.put("eliSub", agejsonarray);
                    redflg = 1;
                }
                /*************************** 5) Age section end  ****************************************/


                /*************************** 6) Min age section   ****************************************/
                int atleasetoneeligible = 0;
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    if (applicanttype.equals("G"))
                        continue;
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    int age = 0;
                    if (vlbre.getApplicantdob() != null) {
                        age = Period.between(vlbre.getApplicantdob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
                        String getColour = vlEligiblityRepository.findEligibleminageecolour(pgm, emptype, age);
                        JSONObject minagedata = new JSONObject(getColour);
                        if (minagedata.get("color").equals("red")) {
                            minagedata.put("applicantId", applicantid);
                            minagedata.put("applicantType", applicanttype);
                            minagedata.put("applicantName", vlbre.getApplname());
                            minagejsonarray.put(minagedata);
                        } else {
                            atleasetoneeligible = 1;
                            break;
                        }
                    }
                }
                if (atleasetoneeligible == 0) {
                    minagejson.put("eliCode", "ELI006");
                    minagejson.put("eliDesc", "Minimum age of atleast 1 applicant or co-applicant not met");
                    minagejson.put("color", "red");
                    minagejson.put("generic", "N");
                    minagejson.put("eliSub", minagejsonarray);
                    redflg = 1;
                }
                /*************************** 6) Min age section end  ****************************************/


                /*************************** 7) Min total emp/bus section   ****************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    String incomeconsidered = vlbre.getIncomeconsidered();
                    if (applicanttype.equals("G") || incomeconsidered.equals("N"))
                        continue;
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    String totalemp = vlbre.getTotalexperience();
                    if (totalemp == null) totalemp = "0";
                    String getColour = vlEligiblityRepository.findEligibleemploymentcolour(pgm, emptype, totalemp);
                    JSONObject totalempdata = new JSONObject(getColour);
                    if (totalempdata.get("color").equals("red")) {
                        totalempdata.put("applicantId", applicantid);
                        totalempdata.put("applicantType", applicanttype);
                        totalempdata.put("applicantName", vlbre.getApplname());
                        totalempjsonarray.put(totalempdata);
                    }
                }
                if (totalempjsonarray.length() > 0) {
                    totalempjson.put("eliCode", "ELI007");
                    totalempjson.put("eliDesc", "Minimum total employment/business experience tenure is not met");
                    totalempjson.put("color", "red");
                    totalempjson.put("generic", "Y");
                    totalempjson.put("eliSub", totalempjsonarray);
                    redflg = 1;
                }
                /*************************** 7) Min Total emp/bus section end  ****************************************/


                /*************************** 8) Min total current emp/bus section   ****************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    String incomeconsidered = vlbre.getIncomeconsidered();
                    if (applicanttype.equals("G") || incomeconsidered.equals("N"))
                        continue;
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    String totalcurremp = "";
                    if (emptype.equalsIgnoreCase("SALARIED") || emptype.equalsIgnoreCase("PENSIONER")) {
                        totalcurremp = vlbre.getCurrentexperience();
                    } else {
                        totalcurremp = vlbre.getCurrentbusinessexperience();
                    }
                    if (totalcurremp == null) totalcurremp = "0";
                    String getColour = vlEligiblityRepository.findEligibleemploymentempcolour(pgm, emptype, totalcurremp);
                    JSONObject totalcurrempdata = new JSONObject(getColour);
                    if (totalcurrempdata.get("color").equals("red")) {
                        totalcurrempdata.put("applicantId", applicantid);
                        totalcurrempdata.put("applicantType", applicanttype);
                        totalcurrempdata.put("applicantName", vlbre.getApplname());
                        totalcurrentempjsonarray.put(totalcurrempdata);
                    }
                }
                if (totalcurrentempjsonarray.length() > 0) {
                    totalcurrentempjson.put("eliCode", "ELI008");
                    totalcurrentempjson.put("eliDesc", "Min current employment tenure is not met");
                    totalcurrentempjson.put("color", "red");
                    totalcurrentempjson.put("generic", "Y");
                    totalcurrentempjson.put("eliSub", totalcurrentempjsonarray);
                    redflg = 1;
                }
                /*************************** 8) Min Total current emp/bus section end  ****************************************/


                /*************************** 9) Min Stay Duration section   ****************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    String comresidencetype =vlbre.getComresidencetype();
                    if (!applicanttype.equals("A"))
                        continue;
                    if(comresidencetype.equalsIgnoreCase("R1")  || comresidencetype.equalsIgnoreCase("R3") )
                        continue;
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    Long stay = vlbre.getStayduration();
                    if (stay == null) stay = 0L;
                    String getColour = vlEligiblityRepository.findEligiblestaycolour(pgm, emptype, stay);
                    JSONObject staydata = new JSONObject(getColour);
                    if (staydata.get("color").equals("red")) {
                        staydata.put("applicantId", applicantid);
                        staydata.put("applicantType", applicanttype);
                        staydata.put("applicantName", vlbre.getApplname());
                        stayjsonarray.put(staydata);
                    }
                }
                if (stayjsonarray.length() > 0) {
                    stayjson.put("eliCode", "ELI009");
                    stayjson.put("eliDesc", "Min duration of stay in current residence is not met by the applicant");
                    stayjson.put("color", "red");
                    stayjson.put("generic", "N");
                    stayjson.put("eliSub", stayjsonarray);
                    redflg = 1;
                }
                /*************************** 9) Min Stay Duration section end  ****************************************/

                /***************************10) DPD Days  Section Start   ********************************************/
                for (VLBREparams vlbre : vlbreparams) {
                    String applicanttype = vlbre.getApplicanttype();
                    Long applicantid = vlbre.getId();
                    String pgm = vlbre.getLoanprogram();
                    if (!ProgramName.equals("") && pgm.equalsIgnoreCase("NONE"))
                        pgm = ProgramName;
                    String emptype = vlbre.getEmptype();
                    Long score = vlbre.getBureauscore();
                    log.info("pgm  is   : {}   applicanttype  : {}   score  {}",pgm,applicanttype,score);
                    if(pgm.equals("70/30")  && score >=700 && score<=739){
                        /**************** New Changes for 70/30 no deleiquecy check for 700 to 739 Score ranges *************/
                        log.info("special Check criteria found");
                        Boolean statNew=false;
                        statNew = getDpdDaysStatFinal(applicantid, pgm, emptype, vlbre.getSlno(), vlbre.getWinum(), "BM");
                        if (statNew) {
                            JSONObject dpddata = new JSONObject();
                            dpddata.put("currentValue", "Not Applicable");
                            dpddata.put("color", "red");
                            dpddata.put("masterValue", "Not Applicable");
                            dpddata.put("applicantId", applicantid);
                            dpddata.put("applicantType", applicanttype);
                            dpddata.put("applicantName", vlbre.getApplname());
                            dpdjsonarray.put(dpddata);
                        }
                    }else{
                        Boolean stat = getDpdDaysStat(applicantid, pgm, emptype, vlbre.getSlno(), vlbre.getWinum(), "BM");
                        log.info(" Final DPD stat " + stat);
                        if (stat) {
                            JSONObject dpddata = new JSONObject();
                            dpddata.put("currentValue", "Not Applicable");
                            dpddata.put("color", "red");
                            dpddata.put("masterValue", "Not Applicable");
                            dpddata.put("applicantId", applicantid);
                            dpddata.put("applicantType", applicanttype);
                            dpddata.put("applicantName", vlbre.getApplname());
                            dpdjsonarray.put(dpddata);
                        }
                    }
                }
                if (dpdjsonarray.length() > 0) {
                    dpdjson.put("eliCode", "ELI011");
                    dpdjson.put("eliDesc", "DPD Days Found in Bureau Report");
                    dpdjson.put("color", "red");
                    dpdjson.put("generic", "N");
                    dpdjson.put("eliSub", dpdjsonarray);
                    redflg = 1;
                }

                /*************************** 10) DPD Duration section end  ****************************************/

                if (redflg > 0) {
                    returnJson.put("status", "failure");
                    if (generaljsonarray.length() > 0) {
                        returnJson.put("ELI000", generaljson1);
                    }
                    if (incomejson.length() > 0) {
                        returnJson.put("ELI001", incomejson);
                    }
                    if (bureaujson.length() > 0) {
                        returnJson.put("ELI002", bureaujson);
                    }
                    if (loanjson.length() > 0) {
                        returnJson.put("ELI003", loanjson);
                    }
                    if (tenurejson.length() > 0) {
                        returnJson.put("ELI004", tenurejson);
                    }
                    if (agejson.length() > 0) {
                        returnJson.put("ELI005", agejson);
                    }
                    if (minagejson.length() > 0) {
                        returnJson.put("ELI006", minagejson);
                    }
                    if (totalempjson.length() > 0) {
                        returnJson.put("ELI007", totalempjson);
                    }
                    if (totalcurrentempjson.length() > 0) {
                        returnJson.put("ELI008", totalcurrentempjson);
                    }
                    if (stayjson.length() > 0) {
                        returnJson.put("ELI009", stayjson);
                    }
                    if (idmatchjson.length() > 0) {
                        returnJson.put("ELI010", idmatchjson);
                    }
                    if (dpdjson.length() > 0) {
                        returnJson.put("ELI011", dpdjson);
                    }
                } else {
                    returnJson.put("status", "success");
                }
                try {
                    if (redflg > 0) {
                        vleligibilitydetails.setEligibilityFlag("red");
                    } else {
                        vleligibilitydetails.setEligibilityFlag("green");
                    }
                    vleligibilitydetails.setSlno(vlbreparams.get(0).getSlno());
                    vleligibilitydetails.setWiNum(vlbreparams.get(0).getWinum());
                    vleligibilitydetails.setQueue(vlbreparams.get(0).getQueue());
                    vleligibilitydetails.setEligibilityRequest(ipnutString);
                    vleligibilitydetails.setCmDate(new Date());
                    vleligibilitydetails.setDelFlg("N");
                    vleligibilitydetails.setEligibilityResponse(returnJson.toString());
                    vleligibilitydetailsrepo.updateDelFlgByWiNumAndSlno(vlbreparams.get(0).getWinum(),vlbreparams.get(0).getSlno());
                    vleligibilitydetailsrepo.save(vleligibilitydetails);
                }catch(Exception ex){
                    ex.printStackTrace();
                    log.info(" Unable to Save Eligibility Data  : {}  ", ex.getMessage());
                }
                log.info(" Final Resp  : {}  ", returnJson.toString());
                return returnJson.toString();

            } else {
                JSONObject resp = new JSONObject();
                resp.put("status", "failure");
                resp.put("eligibilityData", "No Data");
                log.info(" Final Resp  : {}  ", resp.toString());
                return resp.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
            JSONObject resp = new JSONObject();
            resp.put("status", "failure");
            resp.put("eligibilityData", "No Data");
            log.info(" Exception Final Resp  : {}  ", resp.toString());
            return resp.toString();
        }

    }

    public String getMainColor(List <String> colordata) throws Exception {
        if(colordata.stream().filter(color ->"red".equals(color)).count() >0)
            return "red";
        else {
            long amberCount = colordata.stream().filter(color -> "amber".equals(color)).count();
            return amberCount > 0 ? "amber" : "green";
        }
    }

    public String getRaceMainColor(List <String> colordata) throws Exception {
        boolean allRed=colordata.stream().allMatch(color -> "red".equals(color));
        boolean allGreen=colordata.stream().allMatch(color -> "green".equals(color));
        boolean allAmber=colordata.stream().allMatch(color -> "amber".equals(color));
        if(allRed){
            return "red";
        }else if(allGreen){
            return "green";
        }else if(allAmber){
            return "amber";
        }else if(colordata.contains("red") || colordata.contains("amber")){
            return "amber";
        }else
            return "red";

    }

    public String getEligibleMainColor(List <String> colordata) throws Exception {
        long amberCount= colordata.stream().filter(color ->"red".equals(color)).count();
        return amberCount>0 ? "red" : "green";
    }

    public Boolean  getDpdDaysStat(Long appid,String pgm,String emp,Long slno,String winum,String queue){
        String particulars="";
        Boolean stat = false;
        List<Map<String, String>> getDPD = vlEligiblityRepository.finddpddata(pgm,emp,queue);
        log.info("DPD master Data: {}",  getDPD.toString() );
        int duration=0;int dpdDays=0;
        if (!getDPD.isEmpty() && getDPD.size() >= 1) {
            DkDataDTO dkdto = getdkdata(appid, slno,winum);
            if(dkdto!=null && dkdto.getCmdDate() !=null ) {
                String data = dkdto.getDelinquencyAnalysis();
                log.info("data  {}"+ data );
                String datedata = String.valueOf(dkdto.getCmdDate());
                log.info("datedata  {}"+ datedata );
                for (Map<String, String> dpdval : getDPD) {
                    duration=Integer.parseInt(dpdval.get("DURATION"));
                    dpdDays=Integer.parseInt(dpdval.get("MAX_DPD_DAYS"));
                    log.info("duration  {}"+ duration );
                    log.info("dpdDays  {} "+ dpdDays );
                    log.info("appid {} "+appid);
                    if(data != null)
                        stat = validateDpd(duration, dpdDays, datedata, data, winum,slno.toString(),appid.toString(),queue,pgm);
                    else{
                       stat = true;
                        particulars="DK_DATA.delinquency is null";
                        log.info("No data in DK. null  ");
                    }
                    if(stat)
                        break;
                }

            }else{
                stat = true;
                log.info("No data found");
                particulars="No data found in dk_data";
            }
            log.info("stat "+ stat );
        }else{
            if(!pgm.equals("LOANFD")) {
                DkDataDTO dkdto = getdkdata(appid, slno, winum);
                if (dkdto != null && dkdto.getCmdDate() != null) {
                    String data = dkdto.getDelinquencyAnalysis();
                    if (data != null)
                        stat = validateDpdNew(data,queue,pgm,winum,slno.toString(),appid.toString());
                } else {
                    stat = true;
                    log.info("No data found ");
                }
                log.info("stat " + stat);
            }
        }
        if(stat==true && particulars!=null && !particulars.trim().isEmpty()){
            VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
            vehicleLoanBlock.setSlno(slno);
            vehicleLoanBlock.setWiNum(winum);
            vehicleLoanBlock.setApplicantId(appid.toString());
            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SOME_FAILURE);
            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
        }
        return stat;
    }
    public Boolean  getDpdDaysStatFinal(Long appid,String pgm,String emp,Long slno,String winum,String queue){
        Boolean stat = false;
        DkDataDTO dkdto = getdkdata(appid, slno,winum);
        if(dkdto!=null && dkdto.getCmdDate() !=null ) {
            String data = dkdto.getDelinquencyAnalysis();
            log.info("data  {}"+ data );
            if(data != null) {
                stat = validateDpdFinal(data,winum,slno.toString(),appid.toString(),queue,pgm);
            }
            else{
                stat = true;
                log.info("No data in DK. null  ");
            }
        }else{
            stat = true;
            log.info("No data found ");
        }
        log.info("New stat "+ stat );

        return stat;
    }
    public DkDataDTO getdkdata(Long appid,Long slno,String winum) {
        DKData dkdto = new DKData();
        JSONObject jsonObject = new JSONObject();
        DkDataDTO dkresp = new DkDataDTO();
        dkdto =dkrepo.findByAppidAndSlnoAndWinumberAndActiveFlg(String.valueOf(appid),String.valueOf(slno),winum,"Y");
        if(dkdto !=null){
            dkresp.setScore(dkdto.getScoreValue());
            dkresp.setCmdDate(dkdto.getCmdate());
            dkresp.setDelinquencyAnalysis(dkdto.getDelinquencyAnalysis());
            dkresp.setRaceScore(dkdto.getRaceScoreValue());

        }

        return dkresp;
    }
    public boolean validateDpd(int duration, int dpdDays, String dateField, String jsonString, String wiNum, String slno, String applicantId,String queue,String pgm) {
        Boolean status=false;
        String particulars="";
        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
        vehicleLoanBlock.setSlno(Long.parseLong(slno));
        vehicleLoanBlock.setWiNum(wiNum);
        vehicleLoanBlock.setApplicantId(applicantId);

        try {

            log.info("dateField {}",dateField);
            log.info("jsonString {}",jsonString);
            int dotIndex =dateField.indexOf('.');
            if(dotIndex == -1){

            }else{
                dateField=dateField.substring(0,dotIndex);
            }
            log.info("dateField {}",dateField);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateField, dateTimeFormatter);
            DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("ddMMyyyy");
            LocalDate inputDate = dateTime.toLocalDate();
            LocalDate previousDate = inputDate.minusMonths(duration);
            String formdate =dateTime.toLocalDate().format(dateFormatter);
            log.info("inputDate {}",formdate);
            // Calculate the master mmyy based on the duration and dateField
            String masterMmyy = DateTimeFormatter.ofPattern("MMyy").format(previousDate);
            log.info("master Month Year data {}", masterMmyy );
            // Determine the correct key based on dpdDays
            String dpdKey = getDpdKey(dpdDays);
            log.info("dpdKey {}", dpdKey );
            if (dpdKey == null) {
                vehicleLoanBlock.setParticulars("dpdKey is null for dpdDays:"+dpdDays);
                vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SOME_FAILURE);
                vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                return true;  // No matching slab, return true
            }

            // Parse the JSON string
           ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode writeoff = rootNode.get("writeoff");
            JsonNode suitfiled = rootNode.get("suitfiled");
            JsonNode wilful_default = rootNode.get("wilful_default");
            JsonNode PO_Settled = rootNode.get("PO_Settled");
            JsonNode substandard = rootNode.get("substandard");
            JsonNode loss = rootNode.get("loss");
            JsonNode doubtful = rootNode.get("doubtful");
            JsonNode dpdArray = rootNode.get(dpdKey);
            log.info("dpdArray {}", dpdArray );
            if (dpdArray != null && dpdArray.isArray()) {
                for (JsonNode loan : dpdArray) {
                    JsonNode timePeriods = loan.get("timeperiod");
                    if (timePeriods != null && timePeriods.isArray()) {
                        for (JsonNode timePeriod : timePeriods) {
                            int dpdValue = timePeriod.get("dpd").asInt();
                            String mmyy = timePeriod.get("mmyy").asText();
                            log.info("Response dpdValue {}", dpdValue );
                            log.info("Master dpdDays {} ", dpdDays );
                            log.info("Response mmyy  {}", mmyy );
                            log.info("master mmyy {}", masterMmyy );


                            YearMonth yearMonth = YearMonth.parse(masterMmyy, DateTimeFormatter.ofPattern("MMyy"));
                            LocalDate lastDayOfMonthMaster = yearMonth.atEndOfMonth();

                            YearMonth yearMonthmmyy = YearMonth.parse(mmyy, DateTimeFormatter.ofPattern("MMyy"));
                            LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                            // Perform the validation check
                            log.info("lastDayOfMonthMaster {}", lastDayOfMonthMaster );
                            log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy );

                            if ( (dpdValue > dpdDays) && ( lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy) )   ) {
                                log.info("mmyy {} ", mmyy );

                                vehicleLoanBlock.setBlockType(VLBlockCodes.DK_DPDDAYS_FAILURE);
                                vehicleLoanBlock.setParticulars("dpdValue(api):"+dpdValue+",dpdDays(max allowed slab):"+dpdDays+",masterMmyy(slab):"+masterMmyy+",mmyy(api):"+mmyy);
                                log.info("dpdValue(api): {}  dpdDays(max allowed slab) {} masterMmyy(slab): {}  mmyy(api): {}", dpdValue,dpdDays,masterMmyy ,mmyy);
                                vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                return true;  // Validation fails
                            }
                        }
                    }
                }
            }
            if(!pgm.equals("LOANFD")) {
                int durationNew = 0;
                    if (pgm.equals("70/30"))
                        durationNew = Integer.parseInt(iBankService.getMisPRM("70/30_BRE_DURATION").getPVALUE());
                    else if (pgm.equals("INCOME"))
                        durationNew = Integer.parseInt(iBankService.getMisPRM("INCOME_BRE_DURATION").getPVALUE());
                    else if (pgm.equals("SURROGATE"))
                        durationNew = Integer.parseInt(iBankService.getMisPRM("SURROG_BRE_DURATION").getPVALUE());

                    LocalDate previousDateNew = inputDate.minusMonths(durationNew);
                    log.info("duration is  {}  Months", durationNew);
                    String masterMmyyNew = DateTimeFormatter.ofPattern("MMyy").format(previousDateNew);
                    log.info("Master Month Year data is {}", masterMmyyNew);
                    YearMonth yearMonth = YearMonth.parse(masterMmyyNew, DateTimeFormatter.ofPattern("MMyy"));
                    LocalDate lastDayOfMonthMaster = yearMonth.atEndOfMonth();
                    log.info("lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                    if ((writeoff != null && writeoff.isArray() && writeoff.size() >= 1) || (suitfiled != null && suitfiled.isArray() && suitfiled.size() >= 1) || (wilful_default != null && wilful_default.isArray() && wilful_default.size() >= 1) || (PO_Settled != null && PO_Settled.isArray() && PO_Settled.size() >= 1) || (loss != null && loss.isArray() && loss.size() >= 1) || (doubtful != null && doubtful.isArray() && doubtful.size() >= 1) || (substandard != null && substandard.isArray() && substandard.size() >= 1)) {
                        if ((writeoff != null && writeoff.isArray() && writeoff.size() >= 1)) {
                            for (JsonNode writeoffdata : writeoff) {
                                JsonNode timeperiod = writeoffdata.get("timeperiod");
                                if (timeperiod != null && timeperiod.isArray()) {
                                    for (JsonNode timeperioddata : timeperiod) {
                                        String apimmyy = timeperioddata.get("mmyy").asText();
                                        YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                        LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                        log.info("writeoff lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                        log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                        if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                            log.info(" writeoff masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WRITEOFF);
                                            vehicleLoanBlock.setParticulars("writeoff exists");
                                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                            return true;  // Validation fails
                                        }
                                    }
                                }
                            }

                        }
                        if (suitfiled != null && suitfiled.isArray() && suitfiled.size() >= 1) {
                            for (JsonNode suitfileddata : suitfiled) {
                                JsonNode timeperiod = suitfileddata.get("timeperiod");
                                if (timeperiod != null && timeperiod.isArray()) {
                                    for (JsonNode timeperioddata : timeperiod) {
                                        String apimmyy = timeperioddata.get("mmyy").asText();
                                        YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                        LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                        log.info("suitfiled lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                        log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                        if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                            log.info("suitfiled masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUITFILED);
                                            vehicleLoanBlock.setParticulars("suitfiled exists");
                                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                            return true;  // Validation fails
                                        }
                                    }
                                }
                            }
                        }
                        if (wilful_default != null && wilful_default.isArray() && wilful_default.size() >= 1) {
                            for (JsonNode wilful_defaultdata : wilful_default) {
                                JsonNode timeperiod = wilful_defaultdata.get("timeperiod");
                                if (timeperiod != null && timeperiod.isArray()) {
                                    for (JsonNode timeperioddata : timeperiod) {
                                        String apimmyy = timeperioddata.get("mmyy").asText();
                                        YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                        LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                        log.info("wilful_default lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                        log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                        if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                            log.info("wilful_default  masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WILFUL);
                                            vehicleLoanBlock.setParticulars("wilful_default exists");
                                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                            return true;  // Validation fails
                                        }
                                    }
                                }
                            }

                        }
                        if (PO_Settled != null && PO_Settled.isArray() && PO_Settled.size() >= 1) {
                            for (JsonNode PO_Settleddata : PO_Settled) {
                                JsonNode timeperiod = PO_Settleddata.get("timeperiod");
                                if (timeperiod != null && timeperiod.isArray()) {
                                    for (JsonNode timeperioddata : timeperiod) {
                                        String apimmyy = timeperioddata.get("mmyy").asText();
                                        YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                        LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                        log.info("PO_Settled lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                        log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                        if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                            log.info(" PO_Settled masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_PO_Settled);
                                            vehicleLoanBlock.setParticulars("PO_Settled exists");
                                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                            return true;  // Validation fails
                                        }
                                    }
                                }
                            }
                        }
                        if (loss != null && loss.isArray() && loss.size() >= 1) {
                            for (JsonNode lossdata : loss) {
                                JsonNode timeperiod = lossdata.get("timeperiod");
                                if (timeperiod != null && timeperiod.isArray()) {
                                    for (JsonNode timeperioddata : timeperiod) {
                                        String apimmyy = timeperioddata.get("mmyy").asText();
                                        YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                        LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                        log.info("loss lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                        log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                        if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                            log.info("loss  masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_LOSS);
                                            vehicleLoanBlock.setParticulars("loss exists");
                                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                            return true;  // Validation fails
                                        }
                                    }
                                }
                            }

                        }
                        if (doubtful != null && doubtful.isArray() && doubtful.size() >= 1) {
                            for (JsonNode doubtfuldata : doubtful) {
                                JsonNode timeperiod = doubtfuldata.get("timeperiod");
                                if (timeperiod != null && timeperiod.isArray()) {
                                    for (JsonNode timeperioddata : timeperiod) {
                                        String apimmyy = timeperioddata.get("mmyy").asText();
                                        YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                        LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                        log.info("doubtful lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                        log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                        if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                            log.info("doubtful masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_DOUBTFUL);
                                            vehicleLoanBlock.setParticulars("doubtful exists");
                                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                            return true;  // Validation fails
                                        }
                                    }
                                }
                            }

                        }
                        if (substandard != null && substandard.isArray() && substandard.size() >= 1) {
                            for (JsonNode substandarddata : substandard) {
                                JsonNode timeperiod = substandarddata.get("timeperiod");
                                if (timeperiod != null && timeperiod.isArray()) {
                                    for (JsonNode timeperioddata : timeperiod) {
                                        String apimmyy = timeperioddata.get("mmyy").asText();
                                        YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                        LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                        log.info("substandard lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                        log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                        if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                            log.info("substandard masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUBSTANDARD);
                                            vehicleLoanBlock.setParticulars("substandard exists");
                                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                            return true;  // Validation fails
                                        }
                                    }
                                }
                            }

                        }
                        // return true;
                    }
                if(queue.equals("BC")){
                    if ( (writeoff != null && writeoff.isArray() && writeoff.size()>=1) ||  (suitfiled != null && suitfiled.isArray() && suitfiled.size()>=1) || (wilful_default != null && wilful_default.isArray() && wilful_default.size()>=1) ||  (PO_Settled != null && PO_Settled.isArray() && PO_Settled.size()>=1) || (loss != null && loss.isArray() && loss.size()>=1) ||  (doubtful != null && doubtful.isArray() && doubtful.size()>=1) || (substandard!= null && substandard.isArray() && substandard.size()>=1) ) {
                        if((writeoff != null && writeoff.isArray() && writeoff.size()>=1)){
                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WRITEOFF);
                            vehicleLoanBlock.setParticulars("writeoff exists");
                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        }
                        if(suitfiled != null && suitfiled.isArray() && suitfiled.size()>=1){
                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUITFILED);
                            vehicleLoanBlock.setParticulars("suitfiled exists");
                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        }
                        if(wilful_default != null && wilful_default.isArray() && wilful_default.size()>=1){
                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WILFUL);
                            vehicleLoanBlock.setParticulars("wilful_default exists");
                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        }
                        if(PO_Settled != null && PO_Settled.isArray() && PO_Settled.size()>=1){
                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_PO_Settled);
                            vehicleLoanBlock.setParticulars("PO_Settled exists");
                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        }
                        if(loss != null && loss.isArray() && loss.size()>=1){
                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_LOSS);
                            vehicleLoanBlock.setParticulars("loss exists");
                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        }
                        if(doubtful != null && doubtful.isArray() && doubtful.size()>=1){
                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_DOUBTFUL);
                            vehicleLoanBlock.setParticulars("doubtful exists");
                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        }
                        if(substandard!= null && substandard.isArray() && substandard.size()>=1){
                            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUBSTANDARD);
                            vehicleLoanBlock.setParticulars("substandard exists");
                            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        }
                        return true;
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            log.info("DPD Exception {} ", e );
            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SOME_FAILURE);
            vehicleLoanBlock.setParticulars("EXCEPTION:"+e.getMessage());
            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
            return true;  // In case of any error, return false
        }

        return false;  // Validation passes
    }

    public boolean validateDpdNew(String jsonString,String queue,String pgm,String wiNum, String slno, String applicantId) {
        Boolean status=false;
        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
        vehicleLoanBlock.setSlno(Long.parseLong(slno));
        vehicleLoanBlock.setWiNum(wiNum);
        vehicleLoanBlock.setApplicantId(applicantId);
        try {
            log.info("jsonString {} ",jsonString);
            // Parse the JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode writeoff = rootNode.get("writeoff");
            JsonNode suitfiled = rootNode.get("suitfiled");
            JsonNode wilful_default = rootNode.get("wilful_default");
            JsonNode PO_Settled = rootNode.get("PO_Settled");
            JsonNode substandard = rootNode.get("substandard");
            JsonNode loss = rootNode.get("loss");
            JsonNode doubtful = rootNode.get("doubtful");
            int durationNew = 0;
            if (pgm.equals("70/30"))
                durationNew = Integer.parseInt(iBankService.getMisPRM("70/30_BRE_DURATION").getPVALUE());
            else if (pgm.equals("INCOME"))
                durationNew = Integer.parseInt(iBankService.getMisPRM("INCOME_BRE_DURATION").getPVALUE());
            else if (pgm.equals("SURROGATE"))
                durationNew = Integer.parseInt(iBankService.getMisPRM("SURROG_BRE_DURATION").getPVALUE());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateField=String.valueOf(formatter.format(new Date()));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateField, dateTimeFormatter);
            DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("ddMMyyyy");
            LocalDate inputDate = dateTime.toLocalDate();
            LocalDate previousDateNew = inputDate.minusMonths(durationNew);
            log.info("duration is  {}  Months", durationNew);
            String masterMmyyNew = DateTimeFormatter.ofPattern("MMyy").format(previousDateNew);
            log.info("Master Month Year data is {}", masterMmyyNew);
            YearMonth yearMonth = YearMonth.parse(masterMmyyNew, DateTimeFormatter.ofPattern("MMyy"));
            LocalDate lastDayOfMonthMaster = yearMonth.atEndOfMonth();
            log.info("lastDayOfMonthMaster {}", lastDayOfMonthMaster);
            if ((writeoff != null && writeoff.isArray() && writeoff.size() >= 1) || (suitfiled != null && suitfiled.isArray() && suitfiled.size() >= 1) || (wilful_default != null && wilful_default.isArray() && wilful_default.size() >= 1) || (PO_Settled != null && PO_Settled.isArray() && PO_Settled.size() >= 1) || (loss != null && loss.isArray() && loss.size() >= 1) || (doubtful != null && doubtful.isArray() && doubtful.size() >= 1) || (substandard != null && substandard.isArray() && substandard.size() >= 1)) {
                if ((writeoff != null && writeoff.isArray() && writeoff.size() >= 1)) {
                    for (JsonNode writeoffdata : writeoff) {
                        JsonNode timeperiod = writeoffdata.get("timeperiod");
                        if (timeperiod != null && timeperiod.isArray()) {
                            for (JsonNode timeperioddata : timeperiod) {
                                String apimmyy = timeperioddata.get("mmyy").asText();
                                YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                log.info("writeoff lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WRITEOFF);
                                    log.info("writeoff masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                    vehicleLoanBlock.setParticulars("writeoff exists");
                                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                    return true;  // Validation fails
                                }
                            }
                        }
                    }

                }
                if (suitfiled != null && suitfiled.isArray() && suitfiled.size() >= 1) {
                    for (JsonNode suitfileddata : suitfiled) {
                        JsonNode timeperiod = suitfileddata.get("timeperiod");
                        if (timeperiod != null && timeperiod.isArray()) {
                            for (JsonNode timeperioddata : timeperiod) {
                                String apimmyy = timeperioddata.get("mmyy").asText();
                                YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                log.info("suitfiled lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                    log.info("suitfiled masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUITFILED);
                                    vehicleLoanBlock.setParticulars("suitfiled exists");
                                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                    return true;  // Validation fails
                                }
                            }
                        }
                    }
                }
                if (wilful_default != null && wilful_default.isArray() && wilful_default.size() >= 1) {
                    for (JsonNode wilful_defaultdata : wilful_default) {
                        JsonNode timeperiod = wilful_defaultdata.get("timeperiod");
                        if (timeperiod != null && timeperiod.isArray()) {
                            for (JsonNode timeperioddata : timeperiod) {
                                String apimmyy = timeperioddata.get("mmyy").asText();
                                YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                log.info("wilful_default lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                    log.info("wilful_default masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WILFUL);
                                    vehicleLoanBlock.setParticulars("wilful_default exists");
                                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                    return true;  // Validation fails
                                }
                            }
                        }
                    }

                }
                if (PO_Settled != null && PO_Settled.isArray() && PO_Settled.size() >= 1) {
                    for (JsonNode PO_Settleddata : PO_Settled) {
                        JsonNode timeperiod = PO_Settleddata.get("timeperiod");
                        if (timeperiod != null && timeperiod.isArray()) {
                            for (JsonNode timeperioddata : timeperiod) {
                                String apimmyy = timeperioddata.get("mmyy").asText();
                                YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                log.info("PO_Settled lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                    log.info("PO_Settled masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_PO_Settled);
                                    vehicleLoanBlock.setParticulars("PO_Settled exists");
                                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                    return true;  // Validation fails
                                }
                            }
                        }
                    }
                }
                if (loss != null && loss.isArray() && loss.size() >= 1) {
                    for (JsonNode lossdata : loss) {
                        JsonNode timeperiod = lossdata.get("timeperiod");
                        if (timeperiod != null && timeperiod.isArray()) {
                            for (JsonNode timeperioddata : timeperiod) {
                                String apimmyy = timeperioddata.get("mmyy").asText();
                                YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                log.info("loss lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                    log.info("loss  masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_LOSS);
                                    vehicleLoanBlock.setParticulars("loss exists");
                                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                    return true;  // Validation fails
                                }
                            }
                        }
                    }

                }
                if (doubtful != null && doubtful.isArray() && doubtful.size() >= 1) {
                    for (JsonNode doubtfuldata : doubtful) {
                        JsonNode timeperiod = doubtfuldata.get("timeperiod");
                        if (timeperiod != null && timeperiod.isArray()) {
                            for (JsonNode timeperioddata : timeperiod) {
                                String apimmyy = timeperioddata.get("mmyy").asText();
                                YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                log.info("doubtful lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                    log.info("doubtful masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_DOUBTFUL);
                                    vehicleLoanBlock.setParticulars("doubtful exists");
                                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                    return true;  // Validation fails
                                }
                            }
                        }
                    }

                }
                if (substandard != null && substandard.isArray() && substandard.size() >= 1) {
                    for (JsonNode substandarddata : substandard) {
                        JsonNode timeperiod = substandarddata.get("timeperiod");
                        if (timeperiod != null && timeperiod.isArray()) {
                            for (JsonNode timeperioddata : timeperiod) {
                                String apimmyy = timeperioddata.get("mmyy").asText();
                                YearMonth yearMonthmmyy = YearMonth.parse(apimmyy, DateTimeFormatter.ofPattern("MMyy"));
                                LocalDate lastDayOfMonthmmyy = yearMonthmmyy.atEndOfMonth();
                                log.info("substandard lastDayOfMonthMaster {}", lastDayOfMonthMaster);
                                log.info("lastDayOfMonthmmyy {}", lastDayOfMonthmmyy);
                                if (lastDayOfMonthMaster.isBefore(lastDayOfMonthmmyy) || lastDayOfMonthMaster.isEqual(lastDayOfMonthmmyy)) {
                                    log.info("substandard masterMmyy(slab): {}  mmyy(api): {}", masterMmyyNew, apimmyy);
                                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUBSTANDARD);
                                    vehicleLoanBlock.setParticulars("substandard exists");
                                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                                    return true;  // Validation fails
                                }
                            }
                        }
                    }

                }
                // return true;
            }
            if(queue.equals("BC")){
                if ( (writeoff != null && writeoff.isArray() && writeoff.size()>=1) ||  (suitfiled != null && suitfiled.isArray() && suitfiled.size()>=1) || (wilful_default != null && wilful_default.isArray() && wilful_default.size()>=1) ||  (PO_Settled != null && PO_Settled.isArray() && PO_Settled.size()>=1) || (loss != null && loss.isArray() && loss.size()>=1) ||  (doubtful != null && doubtful.isArray() && doubtful.size()>=1) || (substandard!= null && substandard.isArray() && substandard.size()>=1) ) {
                    if((writeoff != null && writeoff.isArray() && writeoff.size()>=1)){
                        vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WRITEOFF);
                        vehicleLoanBlock.setParticulars("writeoff exists");
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    if(suitfiled != null && suitfiled.isArray() && suitfiled.size()>=1){
                        vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUITFILED);
                        vehicleLoanBlock.setParticulars("suitfiled exists");
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    if(wilful_default != null && wilful_default.isArray() && wilful_default.size()>=1){
                        vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WILFUL);
                        vehicleLoanBlock.setParticulars("wilful_default exists");
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    if(PO_Settled != null && PO_Settled.isArray() && PO_Settled.size()>=1){
                        vehicleLoanBlock.setBlockType(VLBlockCodes.DK_PO_Settled);
                        vehicleLoanBlock.setParticulars("PO_Settled exists");
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    if(loss != null && loss.isArray() && loss.size()>=1){
                        vehicleLoanBlock.setBlockType(VLBlockCodes.DK_LOSS);
                        vehicleLoanBlock.setParticulars("loss exists");
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    if(doubtful != null && doubtful.isArray() && doubtful.size()>=1){
                        vehicleLoanBlock.setBlockType(VLBlockCodes.DK_DOUBTFUL);
                        vehicleLoanBlock.setParticulars("doubtful exists");
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    if(substandard!= null && substandard.isArray() && substandard.size()>=1){
                        vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUBSTANDARD);
                        vehicleLoanBlock.setParticulars("substandard exists");
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    return true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception "+jsonString);
            return true;  // In case of any error, return false
        }

        return false;  // Validation passes
    }

    public boolean validateDpdFinal(String jsonString, String wiNum, String slno, String applicantId,String queue,String pgm) {
        Boolean status=false;
        String particulars="";
        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
        vehicleLoanBlock.setSlno(Long.parseLong(slno));
        vehicleLoanBlock.setWiNum(wiNum);
        vehicleLoanBlock.setApplicantId(applicantId);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode writeoff = rootNode.get("writeoff");
            JsonNode suitfiled = rootNode.get("suitfiled");
            JsonNode wilful_default = rootNode.get("wilful_default");
            JsonNode PO_Settled = rootNode.get("PO_Settled");
            JsonNode substandard = rootNode.get("substandard");
            JsonNode loss = rootNode.get("loss");
            JsonNode doubtful = rootNode.get("doubtful");
            JsonNode dpdArray_30 = rootNode.get("dpd_30");
            JsonNode dpdArray_60 = rootNode.get("dpd_60");
            JsonNode dpdArray_90 = rootNode.get("dpd_90");
            log.info("dpdArray_30 {}  dpdArray_60 {}  dpdArray_90 {}", dpdArray_30,dpdArray_60,dpdArray_90 );
            if ( (dpdArray_30 != null && dpdArray_30.isArray()  && dpdArray_30.size() >= 1) || (dpdArray_60 != null && dpdArray_60.isArray() && dpdArray_60.size() >= 1) || (dpdArray_90 != null && dpdArray_90.isArray() && dpdArray_90.size() >= 1 ) ) {
                if((dpdArray_30 != null && dpdArray_30.isArray() && dpdArray_30.size()>=1)){
                    vehicleLoanBlock.setBlockType("DK_DPD_30");
                    vehicleLoanBlock.setParticulars("DPD 30  exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                if(dpdArray_60 != null && dpdArray_60.isArray() && dpdArray_60.size()>=1){
                    vehicleLoanBlock.setBlockType("DK_DPD_60");
                    vehicleLoanBlock.setParticulars("DPD 60  exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                if(dpdArray_90 != null && dpdArray_90.isArray() && dpdArray_90.size()>=1){
                    vehicleLoanBlock.setBlockType("DK_DPD_90");
                    vehicleLoanBlock.setParticulars("DPD 90  exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                return true;
            }
            if ( (writeoff != null && writeoff.isArray() && writeoff.size()>=1) ||  (suitfiled != null && suitfiled.isArray() && suitfiled.size()>=1) || (wilful_default != null && wilful_default.isArray() && wilful_default.size()>=1) ||  (PO_Settled != null && PO_Settled.isArray() && PO_Settled.size()>=1) || (loss != null && loss.isArray() && loss.size()>=1) ||  (doubtful != null && doubtful.isArray() && doubtful.size()>=1) || (substandard!= null && substandard.isArray() && substandard.size()>=1) ) {
                if((writeoff != null && writeoff.isArray() && writeoff.size()>=1)){
                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WRITEOFF);
                    vehicleLoanBlock.setParticulars("writeoff exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                if(suitfiled != null && suitfiled.isArray() && suitfiled.size()>=1){
                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUITFILED);
                    vehicleLoanBlock.setParticulars("suitfiled exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                if(wilful_default != null && wilful_default.isArray() && wilful_default.size()>=1){
                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_WILFUL);
                    vehicleLoanBlock.setParticulars("wilful_default exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                if(PO_Settled != null && PO_Settled.isArray() && PO_Settled.size()>=1){
                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_PO_Settled);
                    vehicleLoanBlock.setParticulars("PO_Settled exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                if(loss != null && loss.isArray() && loss.size()>=1){
                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_LOSS);
                    vehicleLoanBlock.setParticulars("loss exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                if(doubtful != null && doubtful.isArray() && doubtful.size()>=1){
                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_DOUBTFUL);
                    vehicleLoanBlock.setParticulars("doubtful exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                if(substandard!= null && substandard.isArray() && substandard.size()>=1){
                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SUBSTANDARD);
                    vehicleLoanBlock.setParticulars("substandard exists");
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                return true;
            }





        } catch (Exception e) {
            e.printStackTrace();
            log.info("DPD Exception {} ", e );
            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SOME_FAILURE);
            vehicleLoanBlock.setParticulars("EXCEPTION:"+e.getMessage());
            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
            return true;  // In case of any error, return false
        }

        return false;  // Validation passes
    }

    private String getDpdKey(int dpdDays) {
        if (dpdDays >= 30 && dpdDays < 60) {
            return "dpd_30";
        } else if (dpdDays >= 60 && dpdDays < 90) {
            return "dpd_60";
        } else if (dpdDays >= 90 && dpdDays < 120) {
            return "dpd_90";
        }
        return null;
    }


}
