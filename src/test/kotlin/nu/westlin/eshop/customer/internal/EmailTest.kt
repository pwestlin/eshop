package nu.westlin.eshop.customer.internal

import nu.westlin.eshop.customer.internal.Email.Companion.emailRegex
import nu.westlin.eshop.test.isExactlyInstanceOf
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class EmailTest {

    @Test
    fun `correct email`() {
        assertThat(Email("foo@bar.com").value).isEqualTo("foo@bar.com")
    }

    @Test
    fun `incorrect email`() {
        val value = "foo@com"
        assertThatThrownBy { Email(value).value }
            .isExactlyInstanceOf<IllegalArgumentException>()
            .hasMessage("value '$value' does not match regex $emailRegex")
    }
}