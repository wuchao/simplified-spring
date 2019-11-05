/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.util;

import org.springframework.lang.Nullable;

/**
 * Utility methods for simple pattern matching, in particular for
 * Spring's typical "xxx*", "*xxx" and "*xxx*" pattern styles.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class PatternMatchUtils {

	/**
	 * 通配符匹配
	 * Match a String against the given pattern, supporting the following simple
	 * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy" matches (with an
	 * arbitrary number of pattern parts), as well as direct equality.
	 *
	 * @param pattern the pattern to match against
	 * @param str     the String to match
	 * @return whether the String matches the given pattern
	 */
	public static boolean simpleMatch(@Nullable String pattern, @Nullable String str) {
		if (pattern == null || str == null) {
			return false;
		}
		int firstIndex = pattern.indexOf('*');
		if (firstIndex == -1) {
			return pattern.equals(str);
		}
		// pattern 以 * 开头
		if (firstIndex == 0) {
			// 如果 pattern 等于 * ，则返回 true
			if (pattern.length() == 1) {
				return true;
			}
			// 匹配第二个 * 的 index
			int nextIndex = pattern.indexOf('*', firstIndex + 1);
			// 没有匹配到第二个 *
			if (nextIndex == -1) {
				// 则判断 str 是否是以 pattern 中 * 后面的字符串结尾的
				return str.endsWith(pattern.substring(1));
			}
			// 截取 pattern 中两个 * 的中间部分
			String part = pattern.substring(1, nextIndex);
			// 两个 * 中间部分是空字符串
			if (part.isEmpty()) {
				// 则用第二个 * 及其后面的部分再和 str 继续匹配
				return simpleMatch(pattern.substring(nextIndex), str);
			}
			int partIndex = str.indexOf(part);
			//// TODO：？？？？？？
			while (partIndex != -1) {
				// if(simpleMatch(*, str.substring(nextIndex + 1)))
				if (simpleMatch(pattern.substring(nextIndex), str.substring(partIndex + part.length()))) {
					return true;
				}
				// str 中下一个匹配 part 的索引
				partIndex = str.indexOf(part, partIndex + 1);
			}
			return false;
		}
		return (str.length() >= firstIndex &&
				pattern.substring(0, firstIndex).equals(str.substring(0, firstIndex)) &&
				simpleMatch(pattern.substring(firstIndex), str.substring(firstIndex)));
	}

	/**
	 * Match a String against the given patterns, supporting the following simple
	 * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy" matches (with an
	 * arbitrary number of pattern parts), as well as direct equality.
	 *
	 * @param patterns the patterns to match against
	 * @param str      the String to match
	 * @return whether the String matches any of the given patterns
	 */
	public static boolean simpleMatch(@Nullable String[] patterns, String str) {
		if (patterns != null) {
			for (String pattern : patterns) {
				if (simpleMatch(pattern, str)) {
					return true;
				}
			}
		}
		return false;
	}

}
