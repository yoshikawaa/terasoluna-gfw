import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.terasoluna.gfw.common.codelist.ReloadableCodeList;
import org.terasoluna.gfw.common.codelist.i18n.AbstractI18nCodeList;

@RunWith(SpringRunner.class)
public class CodeListTest {

    @Autowired
    private List<ReloadableCodeList> codeLists;
    
    @Test
    public void testCodeLists() {
        
        List<AbstractI18nCodeList> i18nCodeLists = new ArrayList<>();
        for (ReloadableCodeList codeList : codeLists) {
            if (codeList instanceof AbstractI18nCodeList) {
                i18nCodeLists.add((AbstractI18nCodeList) codeList);
            } else {
                codeList.refresh();
            }
        }
        
        for (AbstractI18nCodeList i18nCodeList : i18nCodeLists) {
            i18nCodeList.refresh();
        }
    }


    private Pattern pattern = Pattern.compile("^CL_MONTH");
    
    @Autowired
    private Map<String, ReloadableCodeList> codeListMap;
    
    @Test
    public void testCodeListMap() {
        
        List<AbstractI18nCodeList> i18nCodeLists = new ArrayList<>();
        for (Entry<String, ReloadableCodeList> entry : codeListMap.entrySet()) {

            if (pattern.matcher(entry.getKey()).matches()) {

                ReloadableCodeList codeList = entry.getValue();
                if (codeList instanceof AbstractI18nCodeList) {
                    i18nCodeLists.add((AbstractI18nCodeList) codeList);
                } else {
                    codeList.refresh();
                }
            }
                
        }
        
        for (AbstractI18nCodeList i18nCodeList : i18nCodeLists) {
            i18nCodeList.refresh();
        }
    }
}
