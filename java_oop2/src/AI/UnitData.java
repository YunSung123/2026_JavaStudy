package AI;

import java.util.ArrayList;
import java.util.List;

public class UnitData {

    public static List<Unit> createUnits() {
        List<Unit> units = new ArrayList<>();

        units.add(new Unit("노말", "사루토비", "소환사", "나뭇잎"));
        units.add(new Unit("레어", "모모", "소환사", "악마"));
        units.add(new Unit("레어", "카쿠즈", "소환사", "아카츠키"));
        units.add(new Unit("스페셜", "지라이야", "소환사", "나뭇잎"));
        units.add(new Unit("스페셜", "블러드폴른", "소환사", "흡혈", "창술사"));
        units.add(new Unit("에픽", "나라쿠", "소환사", "요괴"));
        units.add(new Unit("에픽", "드레이크", "소환사", "서번트"));
        units.add(new Unit("레전드", "칼리크로", "소환사", "모래"));
        units.add(new Unit("신화", "치요", "소환사", "모래"));

        units.add(new Unit("노말", "히단", "아카츠키", "창술사"));
        units.add(new Unit("레어", "히루코", "아카츠키", "주술"));
        units.add(new Unit("스페셜", "데이다라", "아카츠키", "화염"));
        units.add(new Unit("에픽", "키사메", "아카츠키", "물"));
        units.add(new Unit("레전드", "야히코", "아카츠키", "주술"));
        units.add(new Unit("레전드", "코난", "아카츠키", "바람"));
        units.add(new Unit("신화", "이타치", "아카츠키", "화염"));
        units.add(new Unit("신화", "토비", "아카츠키", "암살자"));
        units.add(new Unit("울티", "오로치마루", "아카츠키", "봉사장"));

        units.add(new Unit("레어", "이치고", "사신", "검사"));
        units.add(new Unit("스페셜", "시호인", "사신", "격투", "암살자"));
        units.add(new Unit("에픽", "마유리", "사신", "주술"));
        units.add(new Unit("레전드", "토시로", "사신", "물"));
        units.add(new Unit("신화", "켄파치", "사신", "검사"));
        units.add(new Unit("울티", "쿠치키 뱌쿠야", "사신", "봉사장"));

        units.add(new Unit("노말", "메데이아", "서번트", "주술"));
        units.add(new Unit("노말", "란슬롯", "서번트", "검사"));
        units.add(new Unit("레어", "여포", "서번트", "창술사"));
        units.add(new Unit("스페셜", "잭 더 리퍼", "서번트", "암살자"));
        units.add(new Unit("스페셜", "헤라클레스", "서번트", "거인", "검사"));
        units.add(new Unit("에픽", "사키 코지로", "서번트", "검사"));
        units.add(new Unit("레전드", "쿠훌린", "서번트", "창술사"));
        units.add(new Unit("레전드", "프랑켄슈타인", "서번트", "보호"));
        units.add(new Unit("신화", "길가메쉬", "서번트", "연금술"));
        units.add(new Unit("울티", "오지만디아스", "서번트", "봉사장"));

        units.add(new Unit("노말", "요시노", "물", "정령"));
        units.add(new Unit("레어", "아쿠아", "물", "천사"));
        units.add(new Unit("스페셜", "티어 하리벨", "물", "에스파다"));
        units.add(new Unit("레전드", "토비라마", "물", "나뭇잎"));
        units.add(new Unit("신화", "리무르", "물", "악마"));
        units.add(new Unit("울티", "에스데스", "물", "봉사장"));

        units.add(new Unit("노말", "님프", "바람", "천사"));
        units.add(new Unit("노말", "카즈마", "바람", "초능력"));
        units.add(new Unit("레어", "카구라", "바람", "요괴"));
        units.add(new Unit("레어", "네지", "바람", "나뭇잎", "격투"));
        units.add(new Unit("스페셜", "웬디 마벨", "바람", "멸룡"));
        units.add(new Unit("스페셜", "타치바나 카나데", "바람", "천사"));
        units.add(new Unit("에픽", "이자요이 미쿠", "바람", "정령"));
        units.add(new Unit("에픽", "테마리", "바람", "모래"));
        units.add(new Unit("신화", "타츠마키", "바람", "초능력"));

        units.add(new Unit("에픽", "사스케", "나뭇잎", "암살자"));
        units.add(new Unit("에픽", "시카마루", "나뭇잎", "주술"));
        units.add(new Unit("신화", "미나토", "나뭇잎", "암살자"));
        units.add(new Unit("울티", "센주 하시라마", "나뭇잎", "봉사장"));

        units.add(new Unit("노말", "슬로스", "거인", "격투"));
        units.add(new Unit("레어", "알폰스", "거인", "연금술"));
        units.add(new Unit("레전드", "마인부우", "거인", "악마", "연금술"));
        units.add(new Unit("울티", "카이도우", "거인", "봉사장"));

        units.add(new Unit("노말", "그림죠", "에스파다", "검사"));
        units.add(new Unit("레어", "노이트라", "에스파다", "창술사"));
        units.add(new Unit("에픽", "우르키오라", "에스파다", "검사"));
        units.add(new Unit("레전드", "코요테 스타크", "에스파다", "암살자"));

        units.add(new Unit("스페셜", "엔마 아이", "무녀", "요괴"));
        units.add(new Unit("에픽", "아케노", "무녀", "악마"));
        units.add(new Unit("레전드", "키코우", "무녀", "초능력"));
        units.add(new Unit("울티", "하쿠레이", "무녀", "봉사장"));

        units.add(new Unit("노말", "자켄", "화염", "요괴"));
        units.add(new Unit("레어", "이츠카 코토리", "화염", "정령"));
        units.add(new Unit("레전드", "오쿠무라 린", "화염", "악마"));
        units.add(new Unit("울티", "메구밍", "화염", "봉사장"));

        units.add(new Unit("노말", "바비디", "악마", "주술"));
        units.add(new Unit("레전드", "오쿠무라 린", "악마", "화염"));
        units.add(new Unit("울티", "알베도", "악마", "봉사장"));

        units.add(new Unit("스페셜", "시호인", "암살자", "사신", "격투"));
        units.add(new Unit("에픽", "사스케", "암살자", "나뭇잎"));
        units.add(new Unit("신화", "토비", "암살자", "아카츠키"));
        units.add(new Unit("울티", "나나야 시키", "암살자", "봉사장"));

        return units;
    }
}