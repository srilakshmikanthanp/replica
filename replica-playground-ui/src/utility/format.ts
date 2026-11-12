export const formatTime = (epochMillis: number | null) => (epochMillis === null ? '-' : new Date(epochMillis).toLocaleTimeString());
