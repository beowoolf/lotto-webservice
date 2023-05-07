package pl.gry.lotto.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.gry.lotto.BaseIntegrationTest;
import pl.gry.lotto.infrastructure.api.ApiValidationErrorDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiValidationFailedIntegrationTest extends BaseIntegrationTest {

    public static final String INPUT_NUMBERS_ENDPOINT = "/inputNumbers";

    @Test
    public void should_return_400_bad_request_and_validation_message_when_request_has_empty_input_numbers() throws Exception {
        // given & when
        ResultActions perform = mockMvc.perform(post(INPUT_NUMBERS_ENDPOINT)
                .content("""
                        {
                        "inputNumbers" : []
                        }
                        """)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        // then
        MvcResult mvcResult = perform.andExpect(status().isBadRequest()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ApiValidationErrorDto result = objectMapper.readValue(json, ApiValidationErrorDto.class);
        assertThat(result.messages()).containsExactlyInAnyOrder(
                "inputNumbers must not be empty");
    }

    @Test
    public void should_return_400_bad_request_and_validation_message_when_request_does_not_have_input_numbers() throws Exception {
        // given
        // when
        ResultActions perform = mockMvc.perform(post(INPUT_NUMBERS_ENDPOINT)
                .content("""
                        {}
                        """.trim()
                ).contentType(MediaType.APPLICATION_JSON)
        );
        // then
        MvcResult mvcResult = perform.andExpect(status().isBadRequest()).andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        ApiValidationErrorDto result = objectMapper.readValue(json, ApiValidationErrorDto.class);
        assertThat(result.messages()).containsExactlyInAnyOrder(
                "inputNumbers must not be null",
                "inputNumbers must not be empty");
    }

}
