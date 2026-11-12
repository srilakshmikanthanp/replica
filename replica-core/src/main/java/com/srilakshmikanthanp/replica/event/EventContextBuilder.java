package com.srilakshmikanthanp.replica.event;

import java.util.ArrayList;
import java.util.List;

public class EventContextBuilder {
	private final List<EventModule> modules = new ArrayList<>();

	public EventContextBuilder addModules(List<EventModule> modules) {
		this.modules.addAll(modules);
		return this;
	}

	public EventContextBuilder addModule(EventModule module) {
		modules.add(module);
		return this;
	}

	public EventContext build() {
		EventRegistry.Builder eventRegistryBuilder = EventRegistry.builder();
		EventHandlerRegistry.Builder eventHandlerRegistryBuilder = EventHandlerRegistry.builder();

		for (EventModule module : modules) {
			eventRegistryBuilder.add(module.eventRegistry());
			eventHandlerRegistryBuilder.add(module.eventHandlerRegistry());
		}

		EventRegistry eventRegistry = eventRegistryBuilder.build();
		EventHandlerRegistry eventHandlerRegistry = eventHandlerRegistryBuilder.build();
		return new EventContext(eventRegistry, eventHandlerRegistry);
	}
}
