package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;

    @BeforeEach
    public void setUp() {
        강남역 = Station.of("강남역");
        역삼역 = Station.of("역삼역");
        삼성역 = Station.of("삼성역");
        이호선 = Line.of("2호선", "green");
    }

    @Test
    void addSection() {
        이호선.addSection(강남역, 역삼역, 10, 5);
        이호선.addSection(역삼역, 삼성역, 5, 5);

        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역, 삼성역);
    }

    @DisplayName("상행 기준으로 목록 중간에 추가할 경우")
    @Test
    void addSectionInMiddle() {
        이호선.addSection(강남역, 역삼역, 10, 10);
        이호선.addSection(강남역, 삼성역, 5, 5);

        Section section = 이호선.getSections().stream()
            .filter(it -> it.getUpStation() == 강남역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertAll(
            () -> assertThat(이호선.getSections().size()).isEqualTo(2),
            () -> assertThat(section.getDownStation()).isEqualTo(삼성역),
            () -> assertThat(section.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("하행 기준으로 목록 중간에 추가할 경우")
    @Test
    void addSectionInMiddle2() {
        이호선.addSection(강남역, 역삼역, 10, 10);
        이호선.addSection(삼성역, 역삼역, 5, 5);

        Section section = 이호선.getSections().stream()
            .filter(it -> it.getUpStation() == 강남역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertAll(
            () -> assertThat(이호선.getSections().size()).isEqualTo(2),
            () -> assertThat(section.getDownStation()).isEqualTo(삼성역),
            () -> assertThat(section.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("목록 앞에 추가할 경우")
    @Test
    void addSectionInFront() {
        이호선.addSection(강남역, 역삼역, 10, 10);
        이호선.addSection(삼성역, 강남역, 5, 5);

        Section section = 이호선.getSections().stream()
            .filter(it -> it.getUpStation() == 강남역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertAll(
            () -> assertThat(이호선.getSections().size()).isEqualTo(2),
            () -> assertThat(section.getDownStation()).isEqualTo(역삼역),
            () -> assertThat(section.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("목록 뒤에 추가할 경우")
    @Test
    void addSectionBehind() {
        이호선.addSection(강남역, 역삼역, 10, 10);
        이호선.addSection(역삼역, 삼성역, 5, 5);

        Section section = 이호선.getSections().stream()
            .filter(it -> it.getUpStation() == 역삼역)
            .findFirst().orElseThrow(RuntimeException::new);

        assertAll(
            () -> assertThat(이호선.getSections().size()).isEqualTo(2),
            () -> assertThat(section.getDownStation()).isEqualTo(삼성역),
            () -> assertThat(section.getDistance()).isEqualTo(5)
        );
    }

    @Test
    void getStations() {
        이호선.addSection(강남역, 역삼역, 10, 10);
        이호선.addSection(강남역, 삼성역, 5, 5);

        List<Station> result = 이호선.getStations();

        assertThat(result).containsExactly(강남역, 삼성역, 역삼역);
    }

    @DisplayName("이미 존재하는 구간 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        이호선.addSection(강남역, 역삼역, 10, 10);

        assertThatThrownBy(() -> 이호선.addSection(강남역, 역삼역, 5, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void removeSection() {
        이호선.addSection(강남역, 역삼역, 10, 10);
        이호선.addSection(역삼역, 삼성역, 5, 5);

        이호선.deleteSection(삼성역);

        assertThat(이호선.getStations()).containsExactly(강남역, 역삼역);
    }

    @Test
    void removeSectionInFront() {
        이호선.addSection(강남역, 역삼역, 10, 10);
        이호선.addSection(역삼역, 삼성역, 5, 5);

        이호선.deleteSection(강남역);

        assertThat(이호선.getStations()).containsExactly(역삼역, 삼성역);
    }

    @Test
    void removeSectionInMiddle() {
        이호선.addSection(강남역, 역삼역, 10, 10);
        이호선.addSection(역삼역, 삼성역, 5, 5);

        이호선.deleteSection(역삼역);

        assertThat(이호선.getStations()).containsExactly(강남역, 삼성역);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        이호선.addSection(강남역, 역삼역, 10, 10);

        assertThatThrownBy(() -> 이호선.deleteSection(역삼역))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
