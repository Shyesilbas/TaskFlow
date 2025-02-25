export const parseDate = (dateString) => {
    if (!dateString || typeof dateString !== 'string') {
        console.warn('Invalid date string:', dateString);
        return new Date();
    }
    try {
        const [datePart, timePart] = dateString.split(' ');
        const [day, month, year] = datePart.split('-');
        const [hours, minutes] = timePart ? timePart.split(':') : ['00', '00'];
        return new Date(year, month - 1, day, hours, minutes);
    } catch (e) {
        console.error('Failed to parse date:', dateString, e);
        return new Date();
    }
};