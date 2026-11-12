import Typography from '@mui/material/Typography';

export function Payload({ value }: { value: string }) {
	return (
		<Typography title={value} noWrap sx={{ fontFamily: 'monospace', fontSize: 12, maxWidth: { xs: 200, md: 420 } }}>
			{value}
		</Typography>
	);
}
