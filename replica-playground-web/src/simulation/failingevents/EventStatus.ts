import type {EventType} from '../../event/EventType';

export interface EventStatus {
  label: 'Fails' | 'Waits' | 'Applies';
  color: 'error' | 'warning' | 'success';
}

/** The inbox applies messages in order, so while any type fails every other type waits behind it. */
export function eventStatusOf(eventType: EventType, failing: string[]): EventStatus {
  if (failing.includes(eventType.type)) {
    return {label: 'Fails', color: 'error'};
  }

  if (failing.length > 0) {
    return {label: 'Waits', color: 'warning'};
  }

  return {label: 'Applies', color: 'success'};
}
