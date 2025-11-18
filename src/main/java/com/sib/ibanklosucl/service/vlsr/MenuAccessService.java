package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.model.menuaccess.MISHRDesig;
import com.sib.ibanklosucl.model.menuaccess.MISMenuAccessNtLos;
import com.sib.ibanklosucl.dto.MenuList;
import com.sib.ibanklosucl.repository.menuaccess.MISHRDesigRepository;
import com.sib.ibanklosucl.repository.menuaccess.MISMenuAccessNtLosRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MenuAccessService {

    @Autowired
    private UserSessionData usd;

    @Autowired
    private MISMenuAccessNtLosRepository misMenuAccessNtLosRepository;

    @Autowired
    private MISHRDesigRepository misHRDesigRepository;

    public List<MenuList> getAccessibleMenus() {
            String ppcNo = usd.getEmployee().getPpcno();
            String scale = null;

            // Step 1: Determine the scale from MIS_HR_DESIG
            Optional<MISHRDesig> hrDesigOptional = misHRDesigRepository.findById(new MISHRDesig.MISHRDesigId(ppcNo, null));
            if (hrDesigOptional.isPresent()) {
                MISHRDesig hrDesig = hrDesigOptional.get();
                if (hrDesig.getId().getCategory() != null) {
                    scale = hrDesig.getId().getCategory().substring(1, 2);
                }
            }


            // Step 2: Fetch menu access details from MIS_MENU_ACCESS_NT_LOS based on scale
            List<MenuList> accessibleMenus = new ArrayList<>();
        addMenuFromSession(accessibleMenus, usd.getMenuList().stream().filter(mm->mm.getMenuID().equalsIgnoreCase("RPT") || !mm.getMenuID().startsWith("RPT") ).toList());
//            if (scale != null) {
//                List<MISMenuAccessNtLos> menuAccessList = misMenuAccessNtLosRepository.findAll();

//                for (MISMenuAccessNtLos access : menuAccessList) {
//                    if (!"Y".equals(access.getDelFlag())) {
//                        // Iterate through SCALE1 to SCALE13
//                        for (int i = 1; i <= 13; i++) {
//                            if (String.valueOf(i).equals(scale) && "Y".equals(getScaleValue(access, "scale" + i))) {
//
//                            }
//                        }
//
//                        // Check for BRANCHHEAD
//                        if ("Y".equals(access.getBranchHead())) {
//                            addMenuFromSession(accessibleMenus, usd.getMenuList(), "branchMenu");
//                        }
//
//                        // Check for ROHEAD
//                        if ("Y".equals(access.getRoHead())) {
//                            addMenuFromSession(accessibleMenus, usd.getMenuList(), "roMenu");
//                        }
//                    }
//                }
 //        }

            return accessibleMenus;
        }

        private String getScaleValue(MISMenuAccessNtLos access, String scale) {
            try {
                return (String) MISMenuAccessNtLos.class.getDeclaredMethod("get" + capitalize(scale)).invoke(access);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) {
                return str;
            }
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }

        private void addMenuFromSession(List<MenuList> accessibleMenus, List<MenuList> sessionMenus) {
            for (MenuList menu : sessionMenus) {
                    accessibleMenus.add(menu);
            }
        }
    }

