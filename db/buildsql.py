import sys
import csv
import datetime
import xml.dom.minidom
 
if __name__ == "__main__":
   DOMTree = xml.dom.minidom.parse("kendodictionary.xml")
   dictionary = DOMTree.documentElement
   entries = dictionary.getElementsByTagName("entry")

   target = r'导入剑道词典数据sql.txt'
   with open(target, 'a+', newline='',encoding='utf-8') as ftarget:
      writer = csv.writer(ftarget)
      sql = ""   
      for entry in entries:
         print("-----------------------------")
         code = ""
         jkey = ""
         ekey = ""
         jcontent = ""
         econtent = ""
         initial = ""

         keysnode = entry.getElementsByTagName("keys")[0]    
         keys = keysnode.getElementsByTagName("key")
         for key in keys:
            if key.getAttribute('language')=="japanese":
               jkey = key.childNodes[0].data
               print("jkey = " + jkey)
            if key.getAttribute('language')=="english":
               ekey = key.childNodes[0].data
               initial = ekey[0:1]
               print("ekey = " + ekey)
            code = "".join(list(filter(str.isalnum, ekey)))
            print("code = " + code)

         descriptionsnode = entry.getElementsByTagName("descriptions")[0]    
         descriptions = descriptionsnode.getElementsByTagName("description")
         for description in descriptions : 
            if description.getAttribute('language')=="japanese":
               jcontent = description.childNodes[0].data
               jcontent = jcontent.replace("\'","\'\'").replace("’","\'\'")
               print("jcontent = " + jcontent)            
            if description.getAttribute('language')=="english":
               econtent = description.childNodes[0].data
               econtent = econtent.replace("\'","\'\'").replace("’","\'\'")
               print("econtent = " + econtent)

         writer.writerow(['insert into dicentries(code,title,content,language,initial,description,enable) values(\''+code+'\',\''+jkey+'\',\''+jcontent+'\',\'JA\',\''+initial+'\',\'\',1);'])
         writer.writerow(['insert into dicentries(code,title,content,language,initial,description,enable) values(\''+code+'\',\''+ekey+'\',\''+econtent+'\',\'EN\',\''+initial+'\',\'\',1);'])
               

   