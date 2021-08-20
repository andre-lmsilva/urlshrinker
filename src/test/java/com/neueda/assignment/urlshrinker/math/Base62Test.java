package com.neueda.assignment.urlshrinker.math;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Base62Test {

    @Test
    void encode_withValuesSmallerThan62_returnsItsValueBasedOnTheCorrespondentElementInTheCharacterList() {
        for(long index = 0; index < Base62.CHARACTERS.size(); index++) {
            String expectedEncodedValue = Base62.CHARACTERS.get((int)index);
            String result = Base62.encode(index);

            assertThat(result).isEqualTo(expectedEncodedValue);
        }
    }

    @Test
    void encode_withValuesGreaterThan62_returnsItsCalculatedValue() {
        String expectedEncodedValue = Base62.CHARACTERS.get(1) + Base62.CHARACTERS.get(0);

        String result = Base62.encode(62L);

        assertThat(result).isEqualTo(expectedEncodedValue);
    }

    @Test
    void decode_withValuesSmallerThan62_returnsItsOffsetInTheCharacterList() {
        for(String element: Base62.CHARACTERS) {
            Long expectedValue = (long)Base62.CHARACTERS.indexOf(element);
            Long result = Base62.decode(element);

            assertThat(result).isEqualTo(expectedValue);
        }
    }

    @Test
    void decode_withValuesHigherThant62_returnsItsCalculatedValue() {
        String encodedValue = Base62.CHARACTERS.get(1) + Base62.CHARACTERS.get(0);

        Long result = Base62.decode(encodedValue);

        assertThat(result).isEqualTo(62L);
    }


}