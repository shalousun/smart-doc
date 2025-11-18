package io.github.smartdoc.util;

import io.github.smartdoc.utils.JavaClassValidateUtil;
import io.github.smartdoc.utils.DocClassUtil;

import org.junit.jupiter.api.Test;

/**
 * Description: DocUtil junit test
 *
 * @author yu 2018/06/16.
 */
public class DocClassUtilTest {

	@Test
	public void testGetSimpleGicName() {
		char me = 'k';
		String className = "io.github.smartdoc.controller.Teacher<io.github.smartdoc.controller.Teacher<io.github.smartdoc.controller.User,io.github.smartdoc.controller.User,io.github.smartdoc.controller.User>,io.github.smartdoc.controller.Teacher<io.github.smartdoc.controller.User,io.github.smartdoc.controller.User,io.github.smartdoc.controller.User>,io.github.smartdoc.controller.Teacher<io.github.smartdoc.controller.User,io.github.smartdoc.controller.User,io.github.smartdoc.controller.User>>";
		String[] arr = DocClassUtil.getSimpleGicName(className);
		// System.out.println("arr:"+ JSON.toJSONString(arr));
	}

	@Test
	public void testIsPrimitive() {
		String typeName = "java.time.LocalDateTime";
		System.out.println(JavaClassValidateUtil.isPrimitive(typeName));
	}

	@Test
	public void testProcessReturnType() {
		String typeName = "org.springframework.data.domain.Pageable";
		System.out.println(DocClassUtil.rewriteRequestParam(typeName));

	}

}
