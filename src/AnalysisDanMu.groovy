/**
 * 作者： Vance
 * 时间： 2016/10/20
 * 描述： ${todo}.
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
def nameFile = new File('E:/', '昵称.txt')
nameFile.write "斗鱼人数: " + names.size() + '\n\n'

names.each {
    nameFile.append it + '\n'
}


println "人数: " + names.size()