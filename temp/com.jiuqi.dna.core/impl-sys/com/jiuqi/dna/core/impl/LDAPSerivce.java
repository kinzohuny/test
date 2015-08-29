package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.LDAPConfiguration;
import com.jiuqi.dna.core.LDAPValidateTask;
import com.jiuqi.dna.core.None;
import com.jiuqi.dna.core.service.Publish;

final class LDAPSerivce extends ServiceBase<ContextImpl<?, ?, ?>> {

	LDAPSerivce() {
		super("LDAP·þÎñ");
	}

	@Publish
	final class LDAPValidateTaskHandler extends
			TaskMethodHandler<LDAPValidateTask, None> {

		protected LDAPValidateTaskHandler() {
			super(None.NONE, null);
		}

		@Override
		protected final void handle(final ContextImpl<?, ?, ?> context,
				final LDAPValidateTask task) throws Throwable {
			try {
				task.isVerified = context.session.application.LDAPValidator.validate(task.user, task.password);
			} catch (final Throwable throwable) {
				task.isVerified = false;
				task.exception = throwable;
			}
		}
	}

	@Publish
	final class LDAPConfigurationProvider extends
			ResultProvider<LDAPConfiguration> {

		@Override
		protected final LDAPConfiguration provide(
				final ContextImpl<?, ?, ?> context) throws Throwable {
			return context.session.application.LDAPValidator.configuration;
		}

	}

}
