/**
 * ���ߣ� Vance
 * ʱ�䣺 2016/10/20
 * ������ ${todo}.
 */

def file = new File('E:/', 'Example.txt')
lineList = file.readLines()
def names = [] as Set


lineList.each {
    def msg = it.split("\\s+")
    if (msg.size() >= 6) {
        def name = msg[4].split("\\(lv:\\d+\\):")[0]
        names.add(name)
    }
}
def nameFile = new File('E:/', '�ǳ�.txt')
nameFile.write "��������: " + names.size() + '\n\n'

names.each {
    nameFile.append it + '\n'
}


println "����: " + names.size()